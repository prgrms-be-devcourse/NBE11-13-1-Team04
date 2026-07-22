package com.springbeans.cafemenumanagement.order.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class CartOrderRequest {

    private String email;

    private String address;

    private String postalCode;

    private List<CartItem> items;


    @Getter
    public static class CartItem {

        private Long productId;

        private int amount;

    }
}