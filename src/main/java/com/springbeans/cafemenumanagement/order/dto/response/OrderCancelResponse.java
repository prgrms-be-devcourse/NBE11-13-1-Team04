package com.springbeans.cafemenumanagement.order.dto.response;


import com.springbeans.cafemenumanagement.order.entity.OrderStatus;

public record OrderCancelResponse(

        Long orderId,
        OrderStatus status

) {
}