package com.ecommerce.repository;

import com.ecommerce.model.Entitlement;
import com.ecommerce.model.ProductStoreMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntitlementRepository extends JpaRepository<Entitlement, Long> {

    List<Entitlement> findByUserIdAndActiveTrue(Long userId);

    Optional<Entitlement> findByUserIdAndSkuAndStore(Long userId, String sku, ProductStoreMapping.Store store);

    List<Entitlement> findByActiveTrueAndExpiresAtBefore(Instant now);
}
