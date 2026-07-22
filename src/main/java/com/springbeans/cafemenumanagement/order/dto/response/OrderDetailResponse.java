package com.springbeans.cafemenumanagement.order.dto.response;

import com.example.demo.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderDetailResponse(

        Long orderId,
        String email,
        String address,
        String postalCode,
        String orderCode,
        OrderStatus status,
        LocalDateTime orderedAt

) {
}