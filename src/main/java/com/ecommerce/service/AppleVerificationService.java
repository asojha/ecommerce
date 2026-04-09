package com.ecommerce.service;

import com.ecommerce.config.AppleProperties;
import com.ecommerce.model.Entitlement;
import com.ecommerce.model.ProcessedNotification;
import com.ecommerce.model.ProductStoreMapping;
import com.ecommerce.model.Purchase;
import com.ecommerce.repository.EntitlementRepository;
import com.ecommerce.repository.ProcessedNotificationRepository;
import com.ecommerce.repository.ProductStoreMappingRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleVerificationService {

    private final AppleProperties appleProperties;
    private final PurchaseRepository purchaseRepository;
    private final EntitlementRepository entitlementRepository;
    private final ProductStoreMappingRepository storeMappingRepository;
    private final ProcessedNotificationRepository processedNotificationRepository;
    private final EntitlementService entitlementService;
    private final UserLifecycleService userLifecycleService;

    private volatile X509Certificate appleRootCa;

    // ──────────────────────────────────────────────────────────────────────────
    // JWS verification
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Decodes a StoreKit 2 JWS transaction token and returns its claims.
     * The certificate chain embedded in the JWS header is verified against
     * the pinned Apple Root CA G3.
     */
    public JWTClaimsSet verifyAndDecodeJws(String jwsToken) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(jwsToken);

        List<com.nimbusds.jose.util.Base64> x5c = signedJWT.getHeader().getX509CertChain();
        if (x5c == null || x5c.size() < 2) {
            throw new IllegalArgumentException("JWS header must contain an x5c certificate chain");
        }

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] chain = new X509Certificate[x5c.size()];
        for (int i = 0; i < x5c.size(); i++) {
            byte[] certBytes = Base64.getDecoder().decode(x5c.get(i).toString());
            try (InputStream is = new java.io.ByteArrayInputStream(certBytes)) {
                chain[i] = (X509Certificate) cf.generateCertificate(is);
            }
        }

        // Verify chain terminates at Apple Root CA G3
        validateCertChain(chain);

        // Verify JWS signature using leaf certificate public key
        ECPublicKey leafPublicKey = (ECPublicKey) chain[0].getPublicKey();
        ECKey ecKey = new ECKey.Builder(
                com.nimbusds.jose.jwk.Curve.P_256, leafPublicKey).build();
        if (!signedJWT.verify(new ECDSAVerifier(ecKey))) {
            throw new SecurityException("JWS signature verification failed");
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        // Validate bundle ID
        String bundleId = (String) claims.getClaim("bundleId");
        if (!appleProperties.getBundleId().equals(bundleId)) {
            throw new SecurityException(
                    "Bundle ID mismatch: expected " + appleProperties.getBundleId() + ", got " + bundleId);
        }

        return claims;
    }

    private void validateCertChain(X509Certificate[] chain) throws Exception {
        X509Certificate root = getAppleRootCa();
        X509Certificate issuerCert = root;
        // Walk chain from leaf to root
        for (int i = chain.length - 1; i >= 0; i--) {
            chain[i].verify(issuerCert.getPublicKey());
            issuerCert = chain[i];
        }
    }

    private X509Certificate getAppleRootCa() throws Exception {
        if (appleRootCa == null) {
            synchronized (this) {
                if (appleRootCa == null) {
                    ClassPathResource resource = new ClassPathResource("apple/AppleRootCA-G3.cer");
                    try (InputStream is = resource.getInputStream()) {
                        CertificateFactory cf = CertificateFactory.getInstance("X.509");
                        appleRootCa = (X509Certificate) cf.generateCertificate(is);
                    }
                }
            }
        }
        return appleRootCa;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Purchase verification
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public Purchase verifyAndRecordPurchase(Long userId, String jwsToken) throws Exception {
        JWTClaimsSet claims = verifyAndDecodeJws(jwsToken);

        String transactionId = (String) claims.getClaim("transactionId");
        String originalTransactionId = (String) claims.getClaim("originalTransactionId");
        String productId = (String) claims.getClaim("productId");

        // Deduplicate
        Optional<Purchase> existing = purchaseRepository.findByStoreAndStoreTransactionId(
                ProductStoreMapping.Store.APPLE, transactionId);
        if (existing.isPresent()) {
            Purchase dup = existing.get();
            dup.setStatus(Purchase.PurchaseStatus.DUPLICATE);
            return purchaseRepository.save(dup);
        }

        // Resolve our SKU from the Apple product ID
        String sku = storeMappingRepository
                .findByStoreAndStoreProductId(ProductStoreMapping.Store.APPLE, productId)
                .map(m -> m.getSku())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No product mapping found for Apple product ID: " + productId));

        Instant purchasedAt = toInstant(claims.getClaim("purchaseDate"));
        Instant expiresAt = toInstant(claims.getClaim("expiresDate"));

        Purchase purchase = new Purchase();
        purchase.setUserId(userId);
        purchase.setSku(sku);
        purchase.setStore(ProductStoreMapping.Store.APPLE);
        purchase.setStoreTransactionId(transactionId);
        purchase.setStoreOriginalTransactionId(originalTransactionId);
        purchase.setStatus(Purchase.PurchaseStatus.VALIDATED);
        purchase.setPurchasedAt(purchasedAt != null ? purchasedAt : Instant.now());
        purchase.setExpiresAt(expiresAt);
        purchase.setUpdatedAt(Instant.now());
        purchase = purchaseRepository.save(purchase);

        // Create or extend entitlement
        entitlementService.activateEntitlement(userId, sku, ProductStoreMapping.Store.APPLE, expiresAt);
        purchase.setStatus(Purchase.PurchaseStatus.ACTIVE);
        purchase.setUpdatedAt(Instant.now());
        purchase = purchaseRepository.save(purchase);

        // Lifecycle promotion
        userLifecycleService.promoteIfEligible(userId);

        return purchase;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // App Store Server Notifications v2
    // ──────────────────────────────────────────────────────────────────────────

    @Transactional
    public void processNotification(String signedPayload) {
        try {
            JWSObject outerJws = JWSObject.parse(signedPayload);
            // The payload is a JSON string with "signedTransactionInfo" / "signedRenewalInfo"
            String payloadJson = outerJws.getPayload().toString();
            Map<String, Object> payload = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(payloadJson, new com.fasterxml.jackson.core.type.TypeReference<>() {});

            String notificationType = (String) payload.get("notificationType");
            String subtype = (String) payload.get("subtype");
            String notificationUUID = (String) payload.get("notificationUUID");

            // Idempotency: reject duplicates
            if (notificationUUID != null) {
                ProcessedNotification pn = new ProcessedNotification();
                pn.setStore(ProductStoreMapping.Store.APPLE);
                pn.setNotificationId(notificationUUID);
                pn.setNotificationType(notificationType);
                pn.setProcessedAt(Instant.now());
                try {
                    processedNotificationRepository.save(pn);
                } catch (Exception ex) {
                    log.info("Duplicate Apple notification {} — skipping", notificationUUID);
                    return;
                }
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String signedTransactionInfo = data != null ? (String) data.get("signedTransactionInfo") : null;

            if (signedTransactionInfo == null) {
                log.warn("Apple notification {} has no signedTransactionInfo — skipping", notificationType);
                return;
            }

            JWTClaimsSet txClaims = verifyAndDecodeJws(signedTransactionInfo);
            String originalTransactionId = (String) txClaims.getClaim("originalTransactionId");
            String productId = (String) txClaims.getClaim("productId");

            String sku = storeMappingRepository
                    .findByStoreAndStoreProductId(ProductStoreMapping.Store.APPLE, productId)
                    .map(m -> m.getSku())
                    .orElse(null);

            if (sku == null) {
                log.warn("No SKU mapping for Apple product {}", productId);
                return;
            }

            // Find the purchase by original transaction ID
            Optional<Purchase> purchaseOpt = purchaseRepository
                    .findByStoreAndStoreTransactionId(ProductStoreMapping.Store.APPLE, originalTransactionId);

            if (purchaseOpt.isEmpty()) {
                log.warn("Purchase not found for originalTransactionId {}", originalTransactionId);
                return;
            }

            Purchase purchase = purchaseOpt.get();
            Long userId = purchase.getUserId();
            Instant expiresAt = toInstant(txClaims.getClaim("expiresDate"));

            switch (notificationType) {
                case "SUBSCRIBED" -> {
                    entitlementService.activateEntitlement(userId, sku, ProductStoreMapping.Store.APPLE, expiresAt);
                    updatePurchaseStatus(purchase, Purchase.PurchaseStatus.ACTIVE, expiresAt);
                    userLifecycleService.promoteIfEligible(userId);
                }
                case "DID_RENEW" -> {
                    entitlementService.extendEntitlement(userId, sku, ProductStoreMapping.Store.APPLE, expiresAt);
                    updatePurchaseStatus(purchase, Purchase.PurchaseStatus.ACTIVE, expiresAt);
                }
                case "EXPIRED" -> {
                    entitlementService.revokeEntitlement(userId, sku, ProductStoreMapping.Store.APPLE);
                    updatePurchaseStatus(purchase, Purchase.PurchaseStatus.EXPIRED, expiresAt);
                }
                case "DID_FAIL_TO_RENEW" -> {
                    boolean gracePeriod = "GRACE_PERIOD".equals(subtype);
                    Purchase.PurchaseStatus newStatus = gracePeriod
                            ? Purchase.PurchaseStatus.IN_GRACE
                            : Purchase.PurchaseStatus.BILLING_RETRY;
                    updatePurchaseStatus(purchase, newStatus, expiresAt);
                }
                case "GRACE_PERIOD_EXPIRED" -> {
                    entitlementService.revokeEntitlement(userId, sku, ProductStoreMapping.Store.APPLE);
                    updatePurchaseStatus(purchase, Purchase.PurchaseStatus.EXPIRED, expiresAt);
                }
                case "REFUND" -> {
                    entitlementService.revokeEntitlement(userId, sku, ProductStoreMapping.Store.APPLE);
                    updatePurchaseStatus(purchase, Purchase.PurchaseStatus.REFUNDED, expiresAt);
                }
                case "REVOKE" -> {
                    entitlementService.revokeEntitlement(userId, sku, ProductStoreMapping.Store.APPLE);
                    updatePurchaseStatus(purchase, Purchase.PurchaseStatus.EXPIRED, expiresAt);
                }
                case "ONE_TIME_CHARGE" -> {
                    entitlementService.activateEntitlement(userId, sku, ProductStoreMapping.Store.APPLE, null);
                    updatePurchaseStatus(purchase, Purchase.PurchaseStatus.ACTIVE, null);
                    userLifecycleService.promoteIfEligible(userId);
                }
                case "RENEWAL_EXTENDED" -> {
                    entitlementService.extendEntitlement(userId, sku, ProductStoreMapping.Store.APPLE, expiresAt);
                    updatePurchaseStatus(purchase, Purchase.PurchaseStatus.ACTIVE, expiresAt);
                }
                case "DID_CHANGE_RENEWAL_STATUS", "PRICE_INCREASE", "CONSUMPTION_REQUEST" ->
                    log.info("Apple notification type {} / subtype {} — logged only", notificationType, subtype);
                default ->
                    log.warn("Unhandled Apple notification type: {}", notificationType);
            }

        } catch (Exception e) {
            log.error("Failed to process Apple notification", e);
            throw new RuntimeException("Apple notification processing failed", e);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private void updatePurchaseStatus(Purchase purchase, Purchase.PurchaseStatus status, Instant expiresAt) {
        purchase.setStatus(status);
        if (expiresAt != null) purchase.setExpiresAt(expiresAt);
        purchase.setUpdatedAt(Instant.now());
        purchaseRepository.save(purchase);
    }

    private Instant toInstant(Object epochMs) {
        if (epochMs == null) return null;
        if (epochMs instanceof Number n) return Instant.ofEpochMilli(n.longValue());
        return null;
    }
}
