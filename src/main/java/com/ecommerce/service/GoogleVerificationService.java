package com.ecommerce.service;

import com.ecommerce.config.GoogleProperties;
import com.ecommerce.model.Entitlement;
import com.ecommerce.model.ProcessedNotification;
import com.ecommerce.model.ProductStoreMapping;
import com.ecommerce.model.Purchase;
import com.ecommerce.repository.EntitlementRepository;
import com.ecommerce.repository.ProcessedNotificationRepository;
import com.ecommerce.repository.ProductStoreMappingRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.SubscriptionPurchaseV2;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleVerificationService {

    private static final String GOOGLE_JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs";
    private static final String PUBSUB_EMAIL_SUFFIX = "@pubsub.gserviceaccount.com";

    private final GoogleProperties googleProperties;
    private final PurchaseRepository purchaseRepository;
    private final EntitlementRepository entitlementRepository;
    private final ProductStoreMappingRepository storeMappingRepository;
    private final ProcessedNotificationRepository processedNotificationRepository;
    private final EntitlementService entitlementService;
    private final UserLifecycleService userLifecycleService;

    private volatile AndroidPublisher androidPublisher;

    // ──────────────────────────────────────────────────────────────────────────
    // Play Developer API client (lazy init)
    // ──────────────────────────────────────────────────────────────────────────

    private AndroidPublisher getAndroidPublisher() throws Exception {
        if (androidPublisher == null) {
            synchronized (this) {
                if (androidPublisher == null) {
                    GoogleCredentials credentials;
                    String path = googleProperties.getServiceAccountPath();
                    if (path != null && !path.isBlank()) {
                        try (FileInputStream fis = new FileInputStream(path)) {
                            credentials = GoogleCredentials.fromStream(fis)
                                    .createScoped(List.of(AndroidPublisherScopes.ANDROIDPUBLISHER));
                        }
                    } else {
                        credentials = GoogleCredentials.getApplicationDefault()
                                .createScoped(List.of(AndroidPublisherScopes.ANDROIDPUBLISHER));
                    }
                    androidPublisher = new AndroidPublisher.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            GsonFactory.getDefaultInstance(),
                            new HttpCredentialsAdapter(credentials))
                            .setApplicationName("ecommerce-backend")
                            .build();
                }
            }
        }
        return androidPublisher;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Purchase verification
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public Purchase verifyAndRecordPurchase(Long userId, String packageName,
                                             String productId, String purchaseToken) throws Exception {
        // Deduplicate by purchase token
        Optional<Purchase> existing = purchaseRepository.findByStoreAndStoreTransactionId(
                ProductStoreMapping.Store.GOOGLE, purchaseToken);
        if (existing.isPresent()) {
            Purchase dup = existing.get();
            dup.setStatus(Purchase.PurchaseStatus.DUPLICATE);
            return purchaseRepository.save(dup);
        }

        // Resolve our SKU from the Google product ID
        String sku = storeMappingRepository
                .findByStoreAndStoreProductId(ProductStoreMapping.Store.GOOGLE, productId)
                .map(ProductStoreMapping::getSku)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No product mapping found for Google product ID: " + productId));

        // Call Play Developer API (subscriptionsv2.get takes packageName + purchaseToken)
        SubscriptionPurchaseV2 subscription = getAndroidPublisher()
                .purchases()
                .subscriptionsv2()
                .get(packageName, purchaseToken)
                .execute();

        // Check payment state
        String paymentState = subscription.getSubscriptionState();
        if (paymentState == null) {
            throw new IllegalStateException("Could not determine subscription state from Google Play");
        }

        Instant expiresAt = null;
        if (subscription.getLineItems() != null && !subscription.getLineItems().isEmpty()) {
            var lineItem = subscription.getLineItems().get(0);
            if (lineItem.getExpiryTime() != null) {
                expiresAt = Instant.parse(lineItem.getExpiryTime());
            }
        }

        Purchase purchase = new Purchase();
        purchase.setUserId(userId);
        purchase.setSku(sku);
        purchase.setStore(ProductStoreMapping.Store.GOOGLE);
        purchase.setStoreTransactionId(purchaseToken);
        purchase.setStoreOriginalTransactionId(purchaseToken);
        purchase.setStatus(Purchase.PurchaseStatus.VALIDATED);
        purchase.setPurchasedAt(Instant.now());
        purchase.setExpiresAt(expiresAt);
        purchase.setUpdatedAt(Instant.now());
        purchase = purchaseRepository.save(purchase);

        // Acknowledge if needed (must be done within 3 days)
        acknowledgeIfNeeded(packageName, productId, purchaseToken, subscription);

        // Activate entitlement
        entitlementService.activateEntitlement(userId, sku, ProductStoreMapping.Store.GOOGLE, expiresAt);
        purchase.setStatus(Purchase.PurchaseStatus.ACTIVE);
        purchase.setUpdatedAt(Instant.now());
        purchase = purchaseRepository.save(purchase);

        userLifecycleService.promoteIfEligible(userId);

        return purchase;
    }

    private void acknowledgeIfNeeded(String packageName, String productId,
                                      String purchaseToken, SubscriptionPurchaseV2 subscription) {
        try {
            if (subscription.getAcknowledgementState() != null
                    && "ACKNOWLEDGEMENT_STATE_PENDING".equals(subscription.getAcknowledgementState())) {
                // v2 API: acknowledgement via purchases.subscriptions.acknowledge (v1 path)
                getAndroidPublisher()
                        .purchases()
                        .subscriptions()
                        .acknowledge(packageName, productId, purchaseToken,
                                new com.google.api.services.androidpublisher.model.SubscriptionPurchasesAcknowledgeRequest())
                        .execute();
                log.debug("Acknowledged Google purchase token {}", purchaseToken);
            }
        } catch (Exception e) {
            log.warn("Failed to acknowledge Google purchase token {}: {}", purchaseToken, e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Google Play Real-time Developer Notifications (Pub/Sub)
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public void processNotification(String authorizationHeader, String webhookUrl,
                                     Map<String, Object> body) {
        try {
            // Verify Google OIDC JWT from Authorization header
            verifyOidcToken(authorizationHeader, webhookUrl);

            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) body.get("message");
            if (message == null) {
                log.warn("Google Pub/Sub message missing 'message' field");
                return;
            }

            String messageId = (String) message.get("messageId");
            String dataBase64 = (String) message.get("data");
            if (dataBase64 == null) {
                log.warn("Google Pub/Sub message has no data");
                return;
            }

            // Idempotency check
            if (messageId != null) {
                ProcessedNotification pn = new ProcessedNotification();
                pn.setStore(ProductStoreMapping.Store.GOOGLE);
                pn.setNotificationId(messageId);
                pn.setProcessedAt(Instant.now());
                try {
                    processedNotificationRepository.save(pn);
                } catch (Exception ex) {
                    log.info("Duplicate Google notification {} — skipping", messageId);
                    return;
                }
            }

            // Decode Pub/Sub data payload
            String json = new String(Base64.getDecoder().decode(dataBase64));
            @SuppressWarnings("unchecked")
            Map<String, Object> notification = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {});

            String pkg = (String) notification.get("packageName");

            @SuppressWarnings("unchecked")
            Map<String, Object> subNotification =
                    (Map<String, Object>) notification.get("subscriptionNotification");

            if (subNotification == null) {
                log.info("Google notification has no subscriptionNotification — likely a test message");
                return;
            }

            int notifType = ((Number) subNotification.get("notificationType")).intValue();
            String purchaseToken = (String) subNotification.get("purchaseToken");
            String subscriptionId = (String) subNotification.get("subscriptionId");

            String notifTypeName = "TYPE_" + notifType;
            if (messageId != null) {
                processedNotificationRepository.findByStoreAndNotificationId(
                        ProductStoreMapping.Store.GOOGLE, messageId)
                        .ifPresent(pn -> {
                            pn.setNotificationType(notifTypeName);
                            processedNotificationRepository.save(pn);
                        });
            }

            String sku = storeMappingRepository
                    .findByStoreAndStoreProductId(ProductStoreMapping.Store.GOOGLE, subscriptionId)
                    .map(ProductStoreMapping::getSku)
                    .orElse(null);

            if (sku == null) {
                log.warn("No SKU mapping for Google subscription ID {}", subscriptionId);
                return;
            }

            Optional<Purchase> purchaseOpt = purchaseRepository
                    .findByStoreAndStoreTransactionId(ProductStoreMapping.Store.GOOGLE, purchaseToken);

            if (purchaseOpt.isEmpty()) {
                log.warn("Purchase not found for Google purchase token {}", purchaseToken);
                return;
            }

            Purchase purchase = purchaseOpt.get();
            Long userId = purchase.getUserId();

            // Fetch current subscription state for expiry time
            Instant expiresAt = fetchExpiresAt(pkg != null ? pkg : googleProperties.getPackageName(),
                    subscriptionId, purchaseToken);

            handleNotificationType(notifType, userId, sku, purchase, expiresAt);

        } catch (Exception e) {
            log.error("Failed to process Google notification", e);
            throw new RuntimeException("Google notification processing failed", e);
        }
    }

    private void handleNotificationType(int type, Long userId, String sku,
                                         Purchase purchase, Instant expiresAt) {
        switch (type) {
            case 1 -> { // SUBSCRIPTION_RECOVERED
                entitlementService.activateEntitlement(userId, sku, ProductStoreMapping.Store.GOOGLE, expiresAt);
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.ACTIVE, expiresAt);
            }
            case 2 -> { // SUBSCRIPTION_RENEWED
                entitlementService.extendEntitlement(userId, sku, ProductStoreMapping.Store.GOOGLE, expiresAt);
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.ACTIVE, expiresAt);
            }
            case 3 -> // SUBSCRIPTION_CANCELED — flag, do not expire yet
                log.info("Google subscription canceled for user {} sku {} — will expire at {}", userId, sku, expiresAt);
            case 4 -> { // SUBSCRIPTION_PURCHASED
                entitlementService.activateEntitlement(userId, sku, ProductStoreMapping.Store.GOOGLE, expiresAt);
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.ACTIVE, expiresAt);
                userLifecycleService.promoteIfEligible(userId);
            }
            case 5 -> { // SUBSCRIPTION_ON_HOLD
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.BILLING_RETRY, expiresAt);
            }
            case 6 -> { // SUBSCRIPTION_IN_GRACE_PERIOD
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.IN_GRACE, expiresAt);
            }
            case 7 -> { // SUBSCRIPTION_RESTARTED
                entitlementService.activateEntitlement(userId, sku, ProductStoreMapping.Store.GOOGLE, expiresAt);
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.ACTIVE, expiresAt);
            }
            case 8 -> // SUBSCRIPTION_PRICE_CHANGE_CONFIRMED
                log.info("Google price change confirmed for user {} sku {}", userId, sku);
            case 9 -> { // SUBSCRIPTION_DEFERRED
                entitlementService.extendEntitlement(userId, sku, ProductStoreMapping.Store.GOOGLE, expiresAt);
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.ACTIVE, expiresAt);
            }
            case 10 -> { // SUBSCRIPTION_PAUSED
                entitlementService.revokeEntitlement(userId, sku, ProductStoreMapping.Store.GOOGLE);
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.EXPIRED, expiresAt);
            }
            case 11 -> // SUBSCRIPTION_PAUSE_SCHEDULE_CHANGED
                log.info("Google pause schedule changed for user {} sku {}", userId, sku);
            case 12 -> { // SUBSCRIPTION_REVOKED
                entitlementService.revokeEntitlement(userId, sku, ProductStoreMapping.Store.GOOGLE);
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.REFUNDED, expiresAt);
            }
            case 13 -> { // SUBSCRIPTION_EXPIRED
                entitlementService.revokeEntitlement(userId, sku, ProductStoreMapping.Store.GOOGLE);
                updatePurchaseStatus(purchase, Purchase.PurchaseStatus.EXPIRED, expiresAt);
            }
            default -> log.warn("Unhandled Google notification type: {}", type);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // OIDC verification
    // ──────────────────────────────────────────────────────────────────────────

    private void verifyOidcToken(String authorizationHeader, String expectedAudience) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header on Google webhook");
        }
        String token = authorizationHeader.substring(7);

        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        RemoteJWKSet<SecurityContext> jwkSet = new RemoteJWKSet<>(new URL(GOOGLE_JWKS_URL));
        processor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSet));

        JWTClaimsSet claims = processor.process(token, null);

        String email = (String) claims.getClaim("email");
        if (email == null || !email.endsWith(PUBSUB_EMAIL_SUFFIX)) {
            throw new SecurityException("Google OIDC token email not from Pub/Sub service account: " + email);
        }

        if (expectedAudience != null && !expectedAudience.isBlank()) {
            String audience = claims.getAudience() != null && !claims.getAudience().isEmpty()
                    ? claims.getAudience().get(0) : null;
            if (!expectedAudience.equals(audience)) {
                throw new SecurityException("Google OIDC audience mismatch: expected "
                        + expectedAudience + ", got " + audience);
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private Instant fetchExpiresAt(String packageName, String subscriptionId, String purchaseToken) {
        try {
            SubscriptionPurchaseV2 sub = getAndroidPublisher()
                    .purchases()
                    .subscriptionsv2()
                    .get(packageName, purchaseToken)
                    .execute();
            if (sub.getLineItems() != null && !sub.getLineItems().isEmpty()) {
                String expiry = sub.getLineItems().get(0).getExpiryTime();
                if (expiry != null) return Instant.parse(expiry);
            }
        } catch (Exception e) {
            log.warn("Could not fetch expiry time for token {}: {}", purchaseToken, e.getMessage());
        }
        return null;
    }

    private void updatePurchaseStatus(Purchase purchase, Purchase.PurchaseStatus status, Instant expiresAt) {
        purchase.setStatus(status);
        if (expiresAt != null) purchase.setExpiresAt(expiresAt);
        purchase.setUpdatedAt(Instant.now());
        purchaseRepository.save(purchase);
    }
}
