package com.ecommerce.dto;

import com.ecommerce.model.Product;
import com.ecommerce.model.ProductStoreMapping;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Data
public class MobileProductResponse {

    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String productType;
    private String appleProductId;
    private List<String> tags;
    private String imageUrl;

    public static MobileProductResponse from(Product product,
                                              List<ProductStoreMapping> mappings) {
        MobileProductResponse r = new MobileProductResponse();
        r.setSku(product.getSku());
        r.setName(product.getName());
        r.setDescription(product.getDescription());
        r.setPrice(product.getPrice());
        r.setCategory(product.getCategory() != null ? product.getCategory().name() : null);
        r.setProductType(product.getClass().getSimpleName());
        r.setTags(product.getTags());
        r.setImageUrl(product.getImageUrl());

        Optional<ProductStoreMapping> appleMapping = mappings.stream()
                .filter(m -> m.getStore() == ProductStoreMapping.Store.APPLE)
                .findFirst();
        appleMapping.ifPresent(m -> r.setAppleProductId(m.getStoreProductId()));

        return r;
    }
}
