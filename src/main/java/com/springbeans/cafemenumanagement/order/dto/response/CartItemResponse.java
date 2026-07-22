package com.springbeans.cafemenumanagement.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CartItemResponse {

    private Long productId;

    private String productName;

    private int price;

    private int quantity;

}