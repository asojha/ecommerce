package com.ecommerce.dto;

import com.ecommerce.model.Product;
import com.ecommerce.model.SubscriptionProduct;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductResponse {

    private String sku;
    private String productType;
    private String name;
    private String description;
    private BigDecimal price;
    private Product.Category category;
    private List<String> tags;
    private String imageUrl;
    private int matchScore;

    // Subscription-only fields (null for standard products)
    private SubscriptionProduct.BillingCycle billingCycle;
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
