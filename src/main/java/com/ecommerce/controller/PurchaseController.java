package com.ecommerce.controller;

import com.ecommerce.dto.ApplePurchaseRequest;
import com.ecommerce.dto.GooglePurchaseRequest;
import com.ecommerce.dto.PurchaseResponse;
import com.ecommerce.model.Purchase;
import com.ecommerce.security.MobileUserPrincipal;
import com.ecommerce.service.AppleVerificationService;
import com.ecommerce.service.GoogleVerificationService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/mobile/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final AppleVerificationService appleVerificationService;
    private final GoogleVerificationService googleVerificationService;

    // Per-user rate-limit buckets: 10 requests / user / minute
    private final Map<Long, Bucket> buckets = new ConcurrentHashMap<>();

    @PostMapping("/apple")
    public PurchaseResponse verifyApplePurchase(
            @AuthenticationPrincipal MobileUserPrincipal principal,
            @Valid @RequestBody ApplePurchaseRequest req) {

        if (!getBucket(principal.userId()).tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded: 10 purchase verifications per minute");
        }

        try {
            Purchase purchase = appleVerificationService.verifyAndRecordPurchase(
                    principal.userId(), req.getJwsTransactionToken());
            return PurchaseResponse.from(purchase);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Purchase verification failed: " + e.getMessage());
        }
    }

    @PostMapping("/google")
    public PurchaseResponse verifyGooglePurchase(
            @AuthenticationPrincipal MobileUserPrincipal principal,
            @Valid @RequestBody GooglePurchaseRequest req) {

        if (!getBucket(principal.userId()).tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded: 10 purchase verifications per minute");
        }

        try {
            Purchase purchase = googleVerificationService.verifyAndRecordPurchase(
                    principal.userId(), req.getPackageName(),
                    req.getProductId(), req.getPurchaseToken());
            return PurchaseResponse.from(purchase);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Purchase verification failed: " + e.getMessage());
        }
    }

    private Bucket getBucket(Long userId) {
        return buckets.computeIfAbsent(userId, id ->
                Bucket.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(10)
                                .refillGreedy(10, Duration.ofMinutes(1))
                                .build())
                        .build());
    }
}
