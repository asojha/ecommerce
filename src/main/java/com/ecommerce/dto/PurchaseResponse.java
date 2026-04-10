package com.ecommerce.dto;

import com.ecommerce.model.Purchase;
import lombok.Data;

import java.time.Instant;

@Data
public class PurchaseResponse {

    private Long purchaseId;
    private String sku;
    private String status;
    private Instant expiresAt;

    public static PurchaseResponse from(Purchase purchase) {
        PurchaseResponse r = new PurchaseResponse();
        r.setPurchaseId(purchase.getId());
        r.setSku(purchase.getSku());
        r.setStatus(purchase.getStatus().name());
        r.setExpiresAt(purchase.getExpiresAt());
        return r;
    }
}
