package com.ecommerce.service;

import com.ecommerce.dto.ProductResponse;
import com.ecommerce.dto.RecommendationResponse;
import com.ecommerce.dto.UserProfileRequest;
import com.ecommerce.model.Product;
import com.ecommerce.model.UserProfile;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRecommendationService {

    private final ProductRepository productRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public RecommendationResponse getRecommendations(UserProfileRequest request) {
        // Persist or update the user profile
        UserProfile profile = upsertProfile(request);

        // Fetch all active products and score them against the profile
        List<Product> allProducts = productRepository.findByActiveTrue();

        List<ProductResponse> ranked = allProducts.stream()
                .map(p -> ProductResponse.from(p, score(p, profile)))
                .filter(p -> p.getMatchScore() > 0)
                .sorted(Comparator.comparingInt(ProductResponse::getMatchScore).reversed())
                .toList();

        return RecommendationResponse.builder()
                .userEmail(profile.getEmail())
                .userName(profile.getName())
                .totalProducts(ranked.size())
                .products(ranked)
                .build();
    }

    /**
     * Scoring rules (each criterion adds points; 0 = product is filtered out):
     *
     * - Age match:          +30  (hard filter if outside range)
     * - Gender match:       +20  (hard filter if gender mismatch)
     * - Income eligibility: +20  (hard filter if income too low)
     * - Interest category:  +20  (bonus if category is in user interests)
     * - Tag overlap:        +5 per shared tag (soft boost)
     */
    private int score(Product product, UserProfile profile) {
        int points = 10; // base score — product is active

        // --- Hard filters ---

        // Age check
        if (profile.getAge() != null) {
            if (product.getMinAge() != null && profile.getAge() < product.getMinAge()) return 0;
            if (product.getMaxAge() != null && profile.getAge() > product.getMaxAge()) return 0;
            points += 30;
        }

        // Gender check
        if (profile.getGender() != null && product.getTargetGender() != null) {
            boolean genderMatch = product.getTargetGender() == Product.Gender.ALL
                    || product.getTargetGender() == profile.getGender();
            if (!genderMatch) return 0;
            points += 20;
        }

        // Income level check
        if (profile.getIncomeLevel() != null && product.getMinIncomeLevel() != null) {
            if (!profile.getIncomeLevel().isAtLeast(product.getMinIncomeLevel())) return 0;
            points += 20;
        }

        // --- Soft boosts ---

        // Category interest match
        if (profile.getInterests() != null && !profile.getInterests().isEmpty()) {
            if (profile.getInterests().contains(product.getCategory())) {
                points += 20;
            }
        }

        // Tag overlap
        if (product.getTags() != null && profile.getInterests() != null) {
            long tagMatches = product.getTags().stream()
                    .filter(tag -> profile.getInterests().stream()
                            .anyMatch(interest -> interest.name().equalsIgnoreCase(tag)))
                    .count();
            points += (int) (tagMatches * 5);
        }

        return points;
    }

    private UserProfile upsertProfile(UserProfileRequest request) {
        UserProfile profile = userProfileRepository.findByEmail(request.getEmail())
                .orElse(new UserProfile());

        profile.setEmail(request.getEmail());
        profile.setName(request.getName());
        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setLocation(request.getLocation());
        profile.setIncomeLevel(request.getIncomeLevel());
        profile.setInterests(request.getInterests());

        return userProfileRepository.save(profile);
    }
}
