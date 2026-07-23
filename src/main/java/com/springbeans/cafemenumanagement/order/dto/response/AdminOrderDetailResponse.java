package com.springbeans.cafemenumanagement.order.dto.response;


import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record AdminOrderDetailResponse(
        Long orderId,
        String orderCode,
        String email,
        String address,
        String postalCode,
        OrderStatus status,
        String statusDescription,
        LocalDateTime orderedAt,
        List<OrderItemResponse> orderItems,
        int totalPrice
) {
    public record OrderItemResponse(
            Long productId,
            String productName,
            String filePath,
            int productPrice,
            int amount,
            int itemTotalPrice
    ) {

    }
}
