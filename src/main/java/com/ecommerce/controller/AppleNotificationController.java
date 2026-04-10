package com.ecommerce.controller;

import com.ecommerce.service.AppleVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/apple")
@RequiredArgsConstructor
@Slf4j
public class AppleNotificationController {

    private final AppleVerificationService appleVerificationService;

    /**
     * Receives App Store Server Notifications v2.
     * The body is a JWS-signed payload (signedPayload string).
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> receive(@RequestBody AppleNotificationBody body) {
        try {
            appleVerificationService.processNotification(body.getSignedPayload());
        } catch (Exception e) {
            log.error("Apple webhook processing error", e);
            // Return 200 to prevent Apple from retrying unprocessable notifications
        }
        return ResponseEntity.ok().build();
    }

    /** Apple sends: {"signedPayload":"<JWS string>"} */
    public static class AppleNotificationBody {
        private String signedPayload;
        public String getSignedPayload() { return signedPayload; }
        public void setSignedPayload(String signedPayload) { this.signedPayload = signedPayload; }
    }
}
