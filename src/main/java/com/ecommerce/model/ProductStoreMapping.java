package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_store_mappings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"sku", "store"}))
@Data
@NoArgsConstructor
public class ProductStoreMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Store store;

    @Column(nullable = false, length = 200)
    private String storeProductId;

    public enum Store {
        APPLE, GOOGLE
    }
}
