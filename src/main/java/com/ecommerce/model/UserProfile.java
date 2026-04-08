package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Product.Gender gender;

    private String location;

    @Enumerated(EnumType.STRING)
    private Product.IncomeLevel incomeLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private List<Product.Category> interests;

    // ── Customer lifecycle ──────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private Product.CustomerStatus customerStatus;

    @Enumerated(EnumType.STRING)
    private Product.LoyaltyTier loyaltyTier;

    /** Total number of completed orders placed by this customer. */
    private Integer orderCount;

    /** How many months since account creation / first purchase. */
    private Integer accountAgeMonths;
}
