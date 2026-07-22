package com.springbeans.cafemenumanagement.order.dto.response;

import com.example.demo.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderSummaryResponse(

        Long orderId,
        String email,
        OrderStatus status,
        LocalDateTime orderedAt

) {
}