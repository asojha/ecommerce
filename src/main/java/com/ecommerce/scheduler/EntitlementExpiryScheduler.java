package com.ecommerce.scheduler;

import com.ecommerce.service.EntitlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntitlementExpiryScheduler {

    private final EntitlementService entitlementService;

    @Scheduled(fixedDelay = 900_000)
    public void expireStaleEntitlements() {
        log.debug("Running entitlement expiry sweep");
        entitlementService.expireStaleEntitlements();
    }
}
