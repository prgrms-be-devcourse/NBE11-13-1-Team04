package com.springbeans.cafemenumanagement.order.domain.entity;

import com.springbeans.cafemenumanagement.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "order_product")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이게 필요한건지 확인
//    private Long productId;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

//    public OrderProduct( Long productId, Integer amount, Order order ) {
//        this.productId = productId;
//        this.order = order;
//        this.amount = amount;
//    }

    public OrderProduct(Product product, Integer amount, Order order) {
        this.product = product;
        this.amount = amount;
        this.order = order;
    }
}