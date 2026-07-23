package com.springbeans.cafemenumanagement.order.domain.repository;

import com.springbeans.cafemenumanagement.order.domain.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository
        extends JpaRepository<OrderProduct, Long> {
}