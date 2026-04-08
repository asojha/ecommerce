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
        UserProfile profile = upsertProfile(request);

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
     * Scoring rules (each criterion adds points; returning 0 filters the product out):
     *
     * Hard filters (any failure → score = 0):
     *   Age in range          +30
     *   Gender match          +20
     *   Income level          +20
     *   Customer status       +20
     *   Loyalty tier          +20
     *
     * Soft boosts (cumulative):
     *   Category in interests +20
     *   Tag overlap           +5 per match
     *   High order count      +15  (≥ 10 orders)
     *   Established account   +10  (≥ 12 months)
     */
    private int score(Product product, UserProfile profile) {
        int points = 10; // base — product is active

        // ── Hard filters ────────────────────────────────────────────────────

        if (profile.getAge() != null) {
            if (product.getMinAge() != null && profile.getAge() < product.getMinAge()) return 0;
            if (product.getMaxAge() != null && profile.getAge() > product.getMaxAge()) return 0;
            points += 30;
        }

        if (profile.getGender() != null && product.getTargetGender() != null) {
            boolean match = product.getTargetGender() == Product.Gender.ALL
                    || product.getTargetGender() == profile.getGender();
            if (!match) return 0;
            points += 20;
        }

        if (profile.getIncomeLevel() != null && product.getMinIncomeLevel() != null) {
            if (!profile.getIncomeLevel().isAtLeast(product.getMinIncomeLevel())) return 0;
            points += 20;
        }

        if (profile.getCustomerStatus() != null && product.getMinCustomerStatus() != null) {
            if (!profile.getCustomerStatus().isAtLeast(product.getMinCustomerStatus())) return 0;
            points += 20;
        }

        if (profile.getLoyaltyTier() != null && product.getMinLoyaltyTier() != null) {
            if (!profile.getLoyaltyTier().isAtLeast(product.getMinLoyaltyTier())) return 0;
            points += 20;
        }

        // ── Soft boosts ──────────────────────────────────────────────────────

        if (profile.getInterests() != null && !profile.getInterests().isEmpty()) {
            if (profile.getInterests().contains(product.getCategory())) {
                points += 20;
            }
        }

        if (product.getTags() != null && profile.getInterests() != null) {
            long tagMatches = product.getTags().stream()
                    .filter(tag -> profile.getInterests().stream()
                            .anyMatch(i -> i.name().equalsIgnoreCase(tag)))
                    .count();
            points += (int) (tagMatches * 5);
        }

        if (profile.getOrderCount() != null && profile.getOrderCount() >= 10) {
            points += 15;
        }

        if (profile.getAccountAgeMonths() != null && profile.getAccountAgeMonths() >= 12) {
            points += 10;
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
        profile.setCustomerStatus(request.getCustomerStatus());
        profile.setLoyaltyTier(request.getLoyaltyTier());
        profile.setOrderCount(request.getOrderCount());
        profile.setAccountAgeMonths(request.getAccountAgeMonths());

        return userProfileRepository.save(profile);
    }
}
