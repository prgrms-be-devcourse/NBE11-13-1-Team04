package com.springbeans.cafemenumanagement.order.dto.response;

import com.example.demo.entity.OrderStatus;

public record OrderCancelResponse(

        Long orderId,
        OrderStatus status

) {
}