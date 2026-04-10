package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLifecycleService {

    private final PurchaseRepository purchaseRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public void promoteIfEligible(Long userId) {
        long count = purchaseRepository.countByUserIdAndStatus(userId, Purchase.PurchaseStatus.ACTIVE);

        Product.CustomerStatus newStatus = customerStatusFor(count);
        Product.LoyaltyTier newTier = loyaltyTierFor(count);

        userProfileRepository.findById(userId).ifPresent(user -> {
            // AT_RISK is set externally and must not be overwritten
            if (user.getCustomerStatus() != Product.CustomerStatus.AT_RISK) {
                user.setCustomerStatus(newStatus);
            }
            user.setLoyaltyTier(newTier);
            userProfileRepository.save(user);
            log.debug("User {} promoted to status={} tier={}", userId, newStatus, newTier);
        });
    }

    private Product.CustomerStatus customerStatusFor(long count) {
        if (count >= 15) return Product.CustomerStatus.VIP;
        if (count >= 5)  return Product.CustomerStatus.LOYAL;
        if (count >= 1)  return Product.CustomerStatus.RETURNING;
        return Product.CustomerStatus.NEW;
    }

    private Product.LoyaltyTier loyaltyTierFor(long count) {
        if (count >= 30) return Product.LoyaltyTier.PLATINUM;
        if (count >= 15) return Product.LoyaltyTier.GOLD;
        if (count >= 7)  return Product.LoyaltyTier.SILVER;
        if (count >= 3)  return Product.LoyaltyTier.BRONZE;
        return Product.LoyaltyTier.NONE;
    }
}
