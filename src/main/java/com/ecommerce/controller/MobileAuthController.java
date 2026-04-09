package com.ecommerce.controller;

import com.ecommerce.dto.MobileLoginRequest;
import com.ecommerce.dto.MobileRegisterRequest;
import com.ecommerce.dto.TokenResponse;
import com.ecommerce.model.UserProfile;
import com.ecommerce.repository.UserProfileRepository;
import com.ecommerce.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/mobile/auth")
@RequiredArgsConstructor
public class MobileAuthController {

    private final UserProfileRepository userProfileRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody MobileRegisterRequest req) {
        if (userProfileRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        UserProfile user = new UserProfile();
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setCustomerStatus(com.ecommerce.model.Product.CustomerStatus.NEW);
        user.setLoyaltyTier(com.ecommerce.model.Product.LoyaltyTier.NONE);
        user.setOrderCount(0);
        user.setAccountAgeMonths(0);
        user = userProfileRepository.save(user);
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TokenResponse(token, jwtService.getExpirySeconds()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody MobileLoginRequest req) {
        UserProfile user = userProfileRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(new TokenResponse(token, jwtService.getExpirySeconds()));
    }
}
