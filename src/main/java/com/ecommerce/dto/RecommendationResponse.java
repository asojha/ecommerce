package com.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Response envelope returned by the recommendation engine")
public class RecommendationResponse {

    @Schema(description = "Email address of the user profile that was scored", example = "jane@example.com")
    private String userEmail;

    @Schema(description = "Display name of the user", example = "Jane Doe")
    private String userName;

    @Schema(description = "Total number of products that passed all hard filters and received a score > 0",
            example = "12")
    private int totalProducts;

    @Schema(description = "Ranked product list, sorted by matchScore descending")
    private List<ProductResponse> products;
}
