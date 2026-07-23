package com.springbeans.cafemenumanagement.order.dto.response;


import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(

        Long orderId,
        String email,
        String address,
        String postalCode,
        String orderCode,
        OrderStatus status,
        LocalDateTime orderedAt,
        List<OrderItemResponse> items,
        int totalPrice

) {
}