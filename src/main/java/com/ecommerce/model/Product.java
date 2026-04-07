package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private Category category;

    // Targeting criteria — null means "any"
    private Integer minAge;
    private Integer maxAge;

    @Enumerated(EnumType.STRING)
    private Gender targetGender;  // null = all genders

    @Enumerated(EnumType.STRING)
    private IncomeLevel minIncomeLevel;  // minimum income tier required

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags;

    private String imageUrl;
    private boolean active = true;

    public enum Category {
        ELECTRONICS, FASHION, HOME_AND_KITCHEN, SPORTS, BEAUTY, BOOKS,
        TOYS, FOOD_AND_GROCERY, AUTOMOTIVE, HEALTH, TRAVEL, FINANCE
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
