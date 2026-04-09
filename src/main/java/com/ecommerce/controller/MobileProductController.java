package com.ecommerce.controller;

import com.ecommerce.dto.MobileProductResponse;
import com.ecommerce.model.Product;
import com.ecommerce.model.ProductStoreMapping;
import com.ecommerce.model.UserProfile;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ProductStoreMappingRepository;
import com.ecommerce.repository.UserProfileRepository;
import com.ecommerce.security.MobileUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/products")
@RequiredArgsConstructor
public class MobileProductController {

    private final ProductRepository productRepository;
    private final ProductStoreMappingRepository storeMappingRepository;
    private final UserProfileRepository userProfileRepository;

    @GetMapping
    public List<MobileProductResponse> listProducts(
            @AuthenticationPrincipal MobileUserPrincipal principal) {

        UserProfile user = userProfileRepository.findById(principal.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return productRepository.findByActiveTrue().stream()
                .filter(p -> isEligible(p, user))
                .map(p -> MobileProductResponse.from(p, storeMappingRepository.findBySku(p.getSku())))
                .toList();
    }

    @GetMapping("/{sku}")
    public MobileProductResponse getProduct(@AuthenticationPrincipal MobileUserPrincipal principal,
                                             @PathVariable String sku) {
        Product product = productRepository.findById(sku)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        UserProfile user = userProfileRepository.findById(principal.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!isEligible(product, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Product not eligible for this user");
        }

        List<ProductStoreMapping> mappings = storeMappingRepository.findBySku(sku);
        return MobileProductResponse.from(product, mappings);
    }

    private boolean isEligible(Product product, UserProfile user) {
        if (user.getAge() != null) {
            if (product.getMinAge() != null && user.getAge() < product.getMinAge()) return false;
            if (product.getMaxAge() != null && user.getAge() > product.getMaxAge()) return false;
        }
        if (user.getGender() != null && product.getTargetGender() != null
                && product.getTargetGender() != Product.Gender.ALL
                && product.getTargetGender() != user.getGender()) return false;
        if (user.getIncomeLevel() != null && product.getMinIncomeLevel() != null
                && !user.getIncomeLevel().isAtLeast(product.getMinIncomeLevel())) return false;
        if (user.getCustomerStatus() != null && product.getMinCustomerStatus() != null
                && !user.getCustomerStatus().isAtLeast(product.getMinCustomerStatus())) return false;
        if (user.getLoyaltyTier() != null && product.getMinLoyaltyTier() != null
                && !user.getLoyaltyTier().isAtLeast(product.getMinLoyaltyTier())) return false;
        return true;
    }
}
