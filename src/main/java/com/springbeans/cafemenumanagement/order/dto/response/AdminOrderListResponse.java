package com.springbeans.cafemenumanagement.order.dto.response;

import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;

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
