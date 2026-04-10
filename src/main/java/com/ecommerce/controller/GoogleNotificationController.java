package com.ecommerce.controller;

import com.ecommerce.service.GoogleVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/google")
@RequiredArgsConstructor
@Slf4j
public class GoogleNotificationController {

    private final GoogleVerificationService googleVerificationService;

    @Value("${google.webhook-url:}")
    private String webhookUrl;

    /**
     * Receives Google Play Real-time Developer Notifications via Pub/Sub push.
     * The Authorization header contains a Google-signed OIDC JWT.
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> receive(
            HttpServletRequest httpRequest,
            @RequestBody Map<String, Object> body) {
        try {
            String authHeader = httpRequest.getHeader("Authorization");
            googleVerificationService.processNotification(authHeader, webhookUrl, body);
        } catch (Exception e) {
            log.error("Google webhook processing error", e);
            // Return 200 to prevent Pub/Sub from retrying unprocessable messages
        }
        return ResponseEntity.ok().build();
    }
}
