package com.ecommerce.dto;

import com.ecommerce.model.ProductStoreMapping;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreMappingRequest {

    @NotBlank
    private String sku;

    @NotNull
    private ProductStoreMapping.Store store;

    @NotBlank
    private String storeProductId;
}
