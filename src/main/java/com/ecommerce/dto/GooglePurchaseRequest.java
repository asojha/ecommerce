package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GooglePurchaseRequest {

    @NotBlank
    private String packageName;

    @NotBlank
    private String productId;

    @NotBlank
    private String purchaseToken;
}
