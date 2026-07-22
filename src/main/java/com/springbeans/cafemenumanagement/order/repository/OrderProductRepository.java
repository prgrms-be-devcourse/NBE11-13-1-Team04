package com.springbeans.cafemenumanagement.order.repository;

import com.springbeans.cafemenumanagement.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository
        extends JpaRepository<Product, Long> {
}