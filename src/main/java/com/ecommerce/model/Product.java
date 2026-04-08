package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("STANDARD")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "productType", defaultImpl = Product.class, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Product.class,             name = "STANDARD"),
    @JsonSubTypes.Type(value = SubscriptionProduct.class, name = "SUBSCRIPTION")
})
@Data
@NoArgsConstructor
public class Product {

    @Id
    @Column(name = "sku", nullable = false, unique = true, length = 20)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Integer minAge;
    private Integer maxAge;

    @Enumerated(EnumType.STRING)
    private Gender targetGender;

    @Enumerated(EnumType.STRING)
    private IncomeLevel minIncomeLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "sku"))
    @Column(name = "tag")
    private List<String> tags;

    private String imageUrl;
    private boolean active = true;

    // ── Customer lifecycle eligibility ──────────────────────────────────────
    /** Minimum lifecycle stage required; null = any customer. */
    @Enumerated(EnumType.STRING)
    private CustomerStatus minCustomerStatus;

    /** Minimum loyalty tier required; null = any tier. */
    @Enumerated(EnumType.STRING)
    private LoyaltyTier minLoyaltyTier;

    public enum Category {
        ELECTRONICS, FASHION, HOME_AND_KITCHEN, SPORTS, BEAUTY, BOOKS,
        TOYS, FOOD_AND_GROCERY, AUTOMOTIVE, HEALTH, TRAVEL, FINANCE;

        public String skuPrefix() {
            return switch (this) {
                case ELECTRONICS      -> "ELEC";
                case FASHION          -> "FASH";
                case HOME_AND_KITCHEN -> "HOME";
                case SPORTS           -> "SPRT";
                case BEAUTY           -> "BEAU";
                case BOOKS            -> "BOOK";
                case TOYS             -> "TOYS";
                case FOOD_AND_GROCERY -> "FOOD";
                case AUTOMOTIVE       -> "AUTO";
                case HEALTH           -> "HLTH";
                case TRAVEL           -> "TRVL";
                case FINANCE          -> "FINC";
            };
        }
    }

    public enum Gender {
        MALE, FEMALE, ALL
    }

    public enum IncomeLevel {
        LOW, MEDIUM, HIGH, PREMIUM;

        public boolean isAtLeast(IncomeLevel other) {
            return this.ordinal() >= other.ordinal();
        }
    }

    /**
     * Customer lifecycle stage.
     * Ordered: NEW < RETURNING < LOYAL < VIP.
     * AT_RISK is a lateral state treated as RETURNING for eligibility checks.
     */
    public enum CustomerStatus {
        NEW, RETURNING, LOYAL, VIP, AT_RISK;

        /** Returns the ordinal used for "at least" comparisons. AT_RISK maps to RETURNING. */
        public int eligibilityOrdinal() {
            return this == AT_RISK ? RETURNING.ordinal() : this.ordinal();
        }

        public boolean isAtLeast(CustomerStatus other) {
            return this.eligibilityOrdinal() >= other.eligibilityOrdinal();
        }
    }

    /**
     * Customer loyalty tier, ordered NONE < BRONZE < SILVER < GOLD < PLATINUM.
     */
    public enum LoyaltyTier {
        NONE, BRONZE, SILVER, GOLD, PLATINUM;

        public boolean isAtLeast(LoyaltyTier other) {
            return this.ordinal() >= other.ordinal();
        }
    }
}
