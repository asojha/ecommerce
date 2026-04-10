package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "entitlements",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "sku", "store"}))
@Data
@NoArgsConstructor
public class Entitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProductStoreMapping.Store store;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private Instant grantedAt;

    /** null = perpetual (one-time purchase) */
    private Instant expiresAt;

    private Instant lastRenewedAt;
}
