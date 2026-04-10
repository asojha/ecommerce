package com.ecommerce.controller;

import com.ecommerce.model.Entitlement;
import com.ecommerce.security.MobileUserPrincipal;
import com.ecommerce.service.EntitlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/entitlements")
@RequiredArgsConstructor
public class EntitlementController {

    private final EntitlementService entitlementService;

    @GetMapping
    public List<Entitlement> getEntitlements(@AuthenticationPrincipal MobileUserPrincipal principal) {
        return entitlementService.getActiveEntitlements(principal.userId());
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal MobileUserPrincipal principal,
                                       @PathVariable Long id) {
        entitlementService.getActiveEntitlements(principal.userId()).stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .ifPresent(e -> entitlementService.revokeEntitlement(
                        e.getUserId(), e.getSku(), e.getStore()));
        return ResponseEntity.noContent().build();
    }
}
