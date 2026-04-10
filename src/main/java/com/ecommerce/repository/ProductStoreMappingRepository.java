package com.ecommerce.repository;

import com.ecommerce.model.ProductStoreMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStoreMappingRepository extends JpaRepository<ProductStoreMapping, Long> {

    List<ProductStoreMapping> findBySku(String sku);

    Optional<ProductStoreMapping> findBySkuAndStore(String sku, ProductStoreMapping.Store store);

    List<ProductStoreMapping> findByStore(ProductStoreMapping.Store store);

    Optional<ProductStoreMapping> findByStoreAndStoreProductId(ProductStoreMapping.Store store, String storeProductId);
}
