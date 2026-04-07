package com.ecommerce.controller;

import com.ecommerce.dto.RecommendationResponse;
import com.ecommerce.dto.UserProfileRequest;
import com.ecommerce.service.ProductRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final ProductRecommendationService recommendationService;

    /**
     * POST /api/recommendations
     *
     * Accepts a user profile and returns a ranked list of applicable products.
     */
    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @Valid @RequestBody UserProfileRequest request) {
        return ResponseEntity.ok(recommendationService.getRecommendations(request));
    }
}
