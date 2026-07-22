package com.springbeans.cafemenumanagement.admin.order.dto;
import com.springbeans.cafemenumanagement.admin.order.domain.entity.OrderStatus;

import java.time.LocalDateTime;

public record AdminOrderListResponse(
        Long orderId,
        String orderCode,
        String email,
        OrderStatus status,
        String statusDescription,
        LocalDateTime orderedAt,
        int totalAmount,
        int totalPrice
) {
}
