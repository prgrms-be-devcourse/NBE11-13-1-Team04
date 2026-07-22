package com.springbeans.cafemenumanagement.product.repository;

import com.springbeans.cafemenumanagement.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByIsActiveTrue();

    Page<Product> findByCategory(
            String category,
            Pageable pageable
    );

    Page<Product> findByIsActiveTrue(
            Pageable pageable
    );
}
