package com.ecommerce.controller;

import com.ecommerce.dto.StoreMappingRequest;
import com.ecommerce.model.ProductStoreMapping;
import com.ecommerce.repository.ProductStoreMappingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/store-mappings")
@RequiredArgsConstructor
public class StoreProductMappingController {

    private final ProductStoreMappingRepository mappingRepository;

    @GetMapping
    public List<ProductStoreMapping> list() {
        return mappingRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<ProductStoreMapping> create(@Valid @RequestBody StoreMappingRequest req) {
        ProductStoreMapping mapping = new ProductStoreMapping();
        mapping.setSku(req.getSku());
        mapping.setStore(req.getStore());
        mapping.setStoreProductId(req.getStoreProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mappingRepository.save(mapping));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mappingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
