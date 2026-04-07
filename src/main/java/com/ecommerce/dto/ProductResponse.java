package com.ecommerce.dto;

import com.ecommerce.model.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Product.Category category;
    private List<String> tags;
    private String imageUrl;
    private int matchScore;  // relevance score for this user profile

    public static ProductResponse from(Product product, int matchScore) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .tags(product.getTags())
                .imageUrl(product.getImageUrl())
                .matchScore(matchScore)
                .build();
    }
}
