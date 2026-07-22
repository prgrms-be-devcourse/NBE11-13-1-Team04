package com.springbeans.cafemenumanagement.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "product_id")
    private Long productId;


    private int quantity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;


    public OrderItem(
            Long productId,
            int quantity,
            Order order
    ) {
        this.productId = productId;
        this.quantity = quantity;
        this.order = order;
    }
}