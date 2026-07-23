package com.springbeans.cafemenumanagement.order.dto.response;

public record OrderItemResponse(

        Long productId,
        String productName,
        int price,
        int amount,
        int subtotal

) {
}