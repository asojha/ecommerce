package com.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecommendationResponse {

    private String userEmail;
    private String userName;
    private int totalProducts;
    private List<ProductResponse> products;
}
