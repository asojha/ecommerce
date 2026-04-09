package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplePurchaseRequest {

    @NotBlank
    private String jwsTransactionToken;
}
