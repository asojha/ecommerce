package com.ecommerce.controller;

import com.ecommerce.dto.RecommendationResponse;
import com.ecommerce.dto.UserProfileRequest;
import com.ecommerce.service.ProductRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Submit a user profile and receive a ranked product list")
public class RecommendationController {

    private final ProductRecommendationService recommendationService;

    @Operation(
        summary = "Get personalised product recommendations",
        description = """
            Accepts a user profile and scores every active product against it.

            **Hard filters** (score → 0, product excluded):
            - Age outside product's `minAge`–`maxAge` range
            - Gender mismatch with product's `targetGender`
            - User income below product's `minIncomeLevel`
            - User `customerStatus` below product's `minCustomerStatus`
            - User `loyaltyTier` below product's `minLoyaltyTier`

            **Soft boosts** (accumulated on top of hard-filter base):
            | Criterion | Points |
            |-----------|--------|
            | Age in range | +30 |
            | Gender match | +20 |
            | Income sufficient | +20 |
            | Customer status eligible | +20 |
            | Loyalty tier eligible | +20 |
            | Category in user interests | +20 |
            | Each matching tag | +5 |
            | Order count ≥ 10 | +15 |
            | Account age ≥ 12 months | +10 |

            Products are returned sorted by score descending. The user profile is upserted by email,
            so repeated calls with the same address update the stored profile in place.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ranked product list returned",
            content = @Content(schema = @Schema(implementation = RecommendationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error in the request body", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @Valid @RequestBody UserProfileRequest request) {
        return ResponseEntity.ok(recommendationService.getRecommendations(request));
    }
}
