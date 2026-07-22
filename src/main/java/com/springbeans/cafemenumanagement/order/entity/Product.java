package com.springbeans.cafemenumanagement.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;


    private int price;


    private int stock;


    public void decreaseStock(int quantity) {

        if (this.stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        this.stock -= quantity;
    }
}