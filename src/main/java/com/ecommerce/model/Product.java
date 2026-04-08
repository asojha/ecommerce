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
}
