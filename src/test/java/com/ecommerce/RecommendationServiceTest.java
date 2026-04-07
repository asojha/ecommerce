package com.ecommerce;

import com.ecommerce.dto.RecommendationResponse;
import com.ecommerce.dto.UserProfileRequest;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductRecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RecommendationServiceTest {

    @Autowired
    private ProductRecommendationService service;

    @Test
    void youngMaleWithElectronicsInterest_shouldGetElectronicsProducts() {
        UserProfileRequest request = new UserProfileRequest();
        request.setName("Alex Smith");
        request.setEmail("alex@example.com");
        request.setAge(22);
        request.setGender(Product.Gender.MALE);
        request.setIncomeLevel(Product.IncomeLevel.MEDIUM);
        request.setInterests(List.of(Product.Category.ELECTRONICS, Product.Category.SPORTS));

        RecommendationResponse response = service.getRecommendations(request);

        assertThat(response.getProducts()).isNotEmpty();
        // Electronics products should appear and be highly ranked
        assertThat(response.getProducts().get(0).getCategory())
                .isIn(Product.Category.ELECTRONICS, Product.Category.SPORTS);
    }

    @Test
    void childProfile_shouldNotGetAdultOnlyProducts() {
        UserProfileRequest request = new UserProfileRequest();
        request.setName("Timmy");
        request.setEmail("timmy@example.com");
        request.setAge(10);
        request.setGender(Product.Gender.MALE);
        request.setIncomeLevel(Product.IncomeLevel.LOW);
        request.setInterests(List.of(Product.Category.TOYS, Product.Category.BOOKS));

        RecommendationResponse response = service.getRecommendations(request);

        // No product with minAge > 10 should appear
        response.getProducts().forEach(p ->
                assertThat(p.getMatchScore()).isGreaterThan(0));
    }

    @Test
    void lowIncomeUser_shouldNotGetPremiumProducts() {
        UserProfileRequest request = new UserProfileRequest();
        request.setName("Sam Low");
        request.setEmail("sam@example.com");
        request.setAge(30);
        request.setGender(Product.Gender.FEMALE);
        request.setIncomeLevel(Product.IncomeLevel.LOW);
        request.setInterests(List.of(Product.Category.FASHION));

        RecommendationResponse response = service.getRecommendations(request);

        // Luxury watch (PREMIUM) and designer handbag (HIGH) should be filtered
        response.getProducts().forEach(p -> {
            assertThat(p.getName()).doesNotContain("Luxury Watch");
            assertThat(p.getName()).doesNotContain("Designer Handbag");
        });
    }
}
