package com.springbeans.cafemenumanagement.order.dto.response;


import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderSummaryResponse(

        Long orderId,
        String email,
        String address,
        OrderStatus status,
        LocalDateTime orderedAt

) {
}