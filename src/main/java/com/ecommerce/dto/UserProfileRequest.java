package com.ecommerce.dto;

import com.ecommerce.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "User profile submitted to the recommendation engine")
public class UserProfileRequest {

    @Schema(description = "User's display name", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "User's email address — used as the unique profile key (upsert)",
            example = "jane@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User's age in years", example = "28", minimum = "0", maximum = "120")
    @Min(value = 0, message = "Age must be non-negative")
    @Max(value = 120, message = "Age must be realistic")
    private Integer age;

    @Schema(description = "User's gender — used for gender-targeted product filtering",
            example = "FEMALE", allowableValues = {"MALE", "FEMALE", "ALL"})
    private Product.Gender gender;

    @Schema(description = "Free-text location string (city, region)", example = "New York")
    private String location;

    @Schema(description = "User's income bracket — products with a higher `minIncomeLevel` are excluded",
            example = "MEDIUM", allowableValues = {"LOW", "MEDIUM", "HIGH", "PREMIUM"})
    private Product.IncomeLevel incomeLevel;

    @Schema(description = "Product categories the user is interested in — matching products receive a +20 boost",
            example = "[\"ELECTRONICS\", \"SPORTS\"]")
    private List<Product.Category> interests;

    @Schema(description = "Customer lifecycle stage — products requiring a higher status are excluded",
            example = "RETURNING",
            allowableValues = {"NEW", "RETURNING", "LOYAL", "VIP", "AT_RISK"})
    private Product.CustomerStatus customerStatus;

    @Schema(description = "Loyalty programme tier — products requiring a higher tier are excluded",
            example = "BRONZE",
            allowableValues = {"NONE", "BRONZE", "SILVER", "GOLD", "PLATINUM"})
    private Product.LoyaltyTier loyaltyTier;

    @Schema(description = "Total number of orders placed by the user. ≥ 10 gives a +15 score boost",
            example = "7", minimum = "0")
    @Min(value = 0, message = "Order count must be non-negative")
    private Integer orderCount;

    @Schema(description = "How long the user has held an account, in months. ≥ 12 gives a +10 score boost",
            example = "14", minimum = "0")
    @Min(value = 0, message = "Account age must be non-negative")
    private Integer accountAgeMonths;
}
