package com.springbeans.cafemenumanagement.order.dto.response;


import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderCreateResponse(

        Long orderId,
        String orderCode,
        OrderStatus status,
        LocalDateTime orderedAt

) {
}