package com.ecommerce.dto;

import com.ecommerce.model.Product;
import com.ecommerce.model.SubscriptionProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Schema(description = "Product included in a recommendation response, enriched with the match score")
public class ProductResponse {

    @Schema(description = "Unique alphanumeric product identifier", example = "ELEC-IP15P")
    private String sku;

    @Schema(description = "Product type discriminator", example = "STANDARD",
            allowableValues = {"STANDARD", "SUBSCRIPTION"})
    private String productType;

    @Schema(description = "Product display name", example = "iPhone 15 Pro")
    private String name;

    @Schema(description = "Optional long-form description", example = "Latest Apple smartphone with A17 chip")
    private String description;

    @Schema(description = "Price in USD. For subscriptions this is the recurring charge per billing cycle",
            example = "999.99")
    private BigDecimal price;

    @Schema(description = "Product category", example = "ELECTRONICS")
    private Product.Category category;

    @Schema(description = "Free-form tags used for soft-boost matching", example = "[\"tech\", \"premium\"]")
    private List<String> tags;

    @Schema(description = "URL of the product image", example = "https://example.com/iphone15pro.jpg")
    private String imageUrl;

    @Schema(description = "Composite recommendation score. Higher is better; 0 means the product was filtered out",
            example = "85")
    private int matchScore;

    @Schema(description = "Billing frequency for subscription products; null for standard products",
            example = "MONTHLY", allowableValues = {"WEEKLY", "MONTHLY", "QUARTERLY", "ANNUAL"})
    private SubscriptionProduct.BillingCycle billingCycle;

    @Schema(description = "Free trial length in days for subscription products; null if no trial or not a subscription",
            example = "30")
    private Integer trialDays;

    public static ProductResponse from(Product product, int matchScore) {
        ProductResponseBuilder builder = ProductResponse.builder()
                .sku(product.getSku())
                .productType(product instanceof SubscriptionProduct ? "SUBSCRIPTION" : "STANDARD")
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .tags(product.getTags())
                .imageUrl(product.getImageUrl())
                .matchScore(matchScore);

        if (product instanceof SubscriptionProduct sub) {
            builder.billingCycle(sub.getBillingCycle())
                   .trialDays(sub.getTrialDays());
        }

        return builder.build();
    }
}
