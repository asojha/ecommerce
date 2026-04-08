package com.ecommerce.dto;

import com.ecommerce.model.Product;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Min(value = 0, message = "Age must be non-negative")
    @Max(value = 120, message = "Age must be realistic")
    private Integer age;

    private Product.Gender gender;

    private String location;

    private Product.IncomeLevel incomeLevel;

    private List<Product.Category> interests;

    // ── Customer lifecycle ──────────────────────────────────────────────────
    private Product.CustomerStatus customerStatus;

    private Product.LoyaltyTier loyaltyTier;

    @Min(value = 0, message = "Order count must be non-negative")
    private Integer orderCount;

    @Min(value = 0, message = "Account age must be non-negative")
    private Integer accountAgeMonths;
}
