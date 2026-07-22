package com.springbeans.cafemenumanagement.order.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class CartOrderRequest {

    private List<CartItem> items;


    @Getter
    public static class CartItem {

        private Long productId;

        private int amount;

    }
}