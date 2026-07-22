package com.springbeans.cafemenumanagement.admin.order.dto;

import com.springbeans.cafemenumanagement.order.entity.OrderStatus;

import java.time.LocalDateTime;

public record AdminOrderListResponse(
        Long orderId,
        String orderCode,
        String email,
        OrderStatus status,
        String statusDescription,
        LocalDateTime orderedAt,
        LocalDateTime completedAt,
        int totalAmount,
        int totalPrice
) {
}
