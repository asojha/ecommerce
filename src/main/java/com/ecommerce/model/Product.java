package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "A standard (one-time purchase) product in the catalogue")
public class Product {

    @Id
    @Column(name = "sku", nullable = false, unique = true, length = 20)
    @Schema(description = "Unique alphanumeric SKU. Auto-generated from category prefix if omitted",
            example = "ELEC-IP15P")
    private String sku;

    @Column(nullable = false)
    @Schema(description = "Product display name", example = "iPhone 15 Pro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Optional long-form description", example = "Latest Apple smartphone with A17 chip")
    private String description;

    @Column(nullable = false)
    @Schema(description = "Price in USD", example = "999.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Product category", example = "ELECTRONICS", requiredMode = Schema.RequiredMode.REQUIRED)
    private Category category;

    @Schema(description = "Minimum age required to be eligible for this product (inclusive); null = no minimum",
            example = "18")
    private Integer minAge;

    @Schema(description = "Maximum age eligible for this product (inclusive); null = no maximum", example = "65")
    private Integer maxAge;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Gender this product targets; null or ALL means no gender restriction",
            example = "ALL", allowableValues = {"MALE", "FEMALE", "ALL"})
    private Gender targetGender;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Minimum income level required; null = any income",
            example = "MEDIUM", allowableValues = {"LOW", "MEDIUM", "HIGH", "PREMIUM"})
    private IncomeLevel minIncomeLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "sku"))
    @Column(name = "tag")
    @Schema(description = "Free-form tags used for soft-boost matching in the recommendation engine",
            example = "[\"tech\", \"premium\"]")
    private List<String> tags;

    @Schema(description = "URL of the product image")
    private String imageUrl;

    @Schema(description = "Whether the product appears in the catalogue and recommendations. " +
                          "DELETE sets this to false (soft delete)", example = "true")
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Minimum customer lifecycle stage required; null = any status",
            example = "RETURNING", allowableValues = {"NEW", "RETURNING", "LOYAL", "VIP", "AT_RISK"})
    private CustomerStatus minCustomerStatus;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Minimum loyalty tier required; null = any tier",
            example = "NONE", allowableValues = {"NONE", "BRONZE", "SILVER", "GOLD", "PLATINUM"})
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
