package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.SubscriptionProduct;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts(boolean includeInactive) {
        return includeInactive
                ? productRepository.findAll()
                : productRepository.findByActiveTrue();
    }

    public Product getProductBySku(String sku) {
        return productRepository.findById(sku)
                .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + sku));
    }

    public Product saveProduct(Product product) {
        if (product.getSku() == null || product.getSku().isBlank()) {
            product.setSku(generateSku(product.getCategory()));
        }
        return productRepository.save(product);
    }

    public Product updateProduct(String sku, Product updated) {
        Product existing = getProductBySku(sku);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setMinAge(updated.getMinAge());
        existing.setMaxAge(updated.getMaxAge());
        existing.setTargetGender(updated.getTargetGender());
        existing.setMinIncomeLevel(updated.getMinIncomeLevel());
        existing.setTags(updated.getTags());
        existing.setImageUrl(updated.getImageUrl());
        existing.setActive(updated.isActive());

        if (existing instanceof SubscriptionProduct existingSub
                && updated instanceof SubscriptionProduct updatedSub) {
            existingSub.setBillingCycle(updatedSub.getBillingCycle());
            existingSub.setTrialDays(updatedSub.getTrialDays());
        }

        return productRepository.save(existing);
    }

    public void deleteProduct(String sku) {
        Product product = getProductBySku(sku);
        product.setActive(false);
        productRepository.save(product);
    }

    /** Generates a SKU like ELEC-A3F7B2 if none is provided. */
    private String generateSku(Product.Category category) {
        String prefix = category != null ? category.skuPrefix() : "PROD";
        String suffix = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        return prefix + "-" + suffix;
    }
}
