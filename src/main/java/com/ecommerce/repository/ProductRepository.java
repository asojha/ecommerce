package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    List<Product> findByCategoryAndActiveTrue(Product.Category category);

    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (p.targetGender = :gender OR p.targetGender = 'ALL' OR p.targetGender IS NULL) " +
           "AND (p.minAge IS NULL OR p.minAge <= :age) " +
           "AND (p.maxAge IS NULL OR p.maxAge >= :age)")
    List<Product> findByGenderAndAge(
            @Param("gender") Product.Gender gender,
            @Param("age") int age);
}
