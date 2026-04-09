package com.ecommerce.repository;

import com.ecommerce.model.Purchase;
import com.ecommerce.model.ProductStoreMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByStoreAndStoreTransactionId(ProductStoreMapping.Store store, String storeTransactionId);

    List<Purchase> findByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, Purchase.PurchaseStatus status);

    long countByUserId(Long userId);
}
