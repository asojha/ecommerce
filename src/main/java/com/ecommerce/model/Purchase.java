package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "purchases",
       uniqueConstraints = @UniqueConstraint(columnNames = {"store", "store_transaction_id"}))
@Data
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProductStoreMapping.Store store;

    @Column(nullable = false, length = 200)
    private String storeTransactionId;

    @Column(length = 200)
    private String storeOriginalTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseStatus status;

    @Column(nullable = false)
    private Instant purchasedAt;

    private Instant expiresAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public enum PurchaseStatus {
        PENDING, VALIDATED, ACTIVE, IN_GRACE, BILLING_RETRY, EXPIRED, REFUNDED, DUPLICATE
    }
}
