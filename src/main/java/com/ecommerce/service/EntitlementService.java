package com.ecommerce.service;

import com.ecommerce.model.Entitlement;
import com.ecommerce.model.ProductStoreMapping;
import com.ecommerce.repository.EntitlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntitlementService {

    private final EntitlementRepository entitlementRepository;

    @Transactional
    public Entitlement activateEntitlement(Long userId, String sku,
                                           ProductStoreMapping.Store store, Instant expiresAt) {
        Optional<Entitlement> existing = entitlementRepository.findByUserIdAndSkuAndStore(userId, sku, store);
        Entitlement entitlement = existing.orElseGet(Entitlement::new);
        entitlement.setUserId(userId);
        entitlement.setSku(sku);
        entitlement.setStore(store);
        entitlement.setActive(true);
        if (entitlement.getGrantedAt() == null) {
            entitlement.setGrantedAt(Instant.now());
        }
        entitlement.setExpiresAt(expiresAt);
        entitlement.setLastRenewedAt(Instant.now());
        return entitlementRepository.save(entitlement);
    }

    @Transactional
    public Entitlement extendEntitlement(Long userId, String sku,
                                         ProductStoreMapping.Store store, Instant newExpiresAt) {
        Entitlement entitlement = entitlementRepository
                .findByUserIdAndSkuAndStore(userId, sku, store)
                .orElseGet(() -> {
                    Entitlement e = new Entitlement();
                    e.setUserId(userId);
                    e.setSku(sku);
                    e.setStore(store);
                    e.setGrantedAt(Instant.now());
                    return e;
                });
        entitlement.setActive(true);
        entitlement.setExpiresAt(newExpiresAt);
        entitlement.setLastRenewedAt(Instant.now());
        return entitlementRepository.save(entitlement);
    }

    @Transactional
    public void revokeEntitlement(Long userId, String sku, ProductStoreMapping.Store store) {
        entitlementRepository.findByUserIdAndSkuAndStore(userId, sku, store).ifPresent(e -> {
            e.setActive(false);
            entitlementRepository.save(e);
        });
    }

    public List<Entitlement> getActiveEntitlements(Long userId) {
        return entitlementRepository.findByUserIdAndActiveTrue(userId);
    }

    @Transactional
    public void expireStaleEntitlements() {
        List<Entitlement> stale = entitlementRepository.findByActiveTrueAndExpiresAtBefore(Instant.now());
        stale.forEach(e -> e.setActive(false));
        entitlementRepository.saveAll(stale);
    }
}
