package com.springbeans.cafemenumanagement.order.service;

import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import com.springbeans.cafemenumanagement.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderProduct;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;
import com.springbeans.cafemenumanagement.order.domain.repository.OrderRepository;
import com.springbeans.cafemenumanagement.order.dto.request.AdminOrderSearchCondition;
import com.springbeans.cafemenumanagement.order.dto.response.AdminOrderDetailResponse;
import com.springbeans.cafemenumanagement.order.dto.response.AdminOrderListResponse;
import com.springbeans.cafemenumanagement.order.exception.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {
    private final OrderRepository orderRepository;

    public List<AdminOrderListResponse> getOrders( AdminOrderSearchCondition condition ) {
        List<Order> orders = orderRepository.findAllByCondition(condition);

        return orders.stream()
                .map(this::convertToAdminOrderListResponse)
                .toList();
    }

    public AdminOrderDetailResponse getOrderDetail( Long orderId ) {
        Order order = orderRepository.findByIdWithProducts(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        return convertToAdminOrderDetailResponse(order);
    }

    private AdminOrderListResponse convertToAdminOrderListResponse( Order order ) {
        List<OrderProduct> orderProducts = order.getOrderProducts() != null ? order.getOrderProducts() : List.of();

        int totalAmount = orderProducts.stream()
                .filter(op -> op != null)
                .mapToInt(OrderProduct::getAmount)
                .sum();

        int totalPrice = orderProducts.stream()
                .filter(op -> op != null && op.getProduct() != null)
                .mapToInt(op -> op.getProduct().getPrice() * op.getAmount())
                .sum();
        String statusDesc = (order.getStatus() != null) ? order.getStatus().getDescription() : "";

        return new AdminOrderListResponse(
                order.getId(),
                order.getOrderCode(),
                order.getEmail(),
                order.getStatus(),
                statusDesc,
                order.getOrderedAt(),
                order.getConfirmedAt(),
                totalAmount,
                totalPrice
        );
    }

    private AdminOrderDetailResponse convertToAdminOrderDetailResponse( Order order ) {
        List<AdminOrderDetailResponse.OrderItemResponse> itemResponses = order.getOrderProducts().stream()
                .map(this::convertToOrderItemResponse)
                .toList();

        int totalPrice = itemResponses.stream()
                .mapToInt(AdminOrderDetailResponse.OrderItemResponse::itemTotalPrice)
                .sum();

        return new AdminOrderDetailResponse(
                order.getId(),
                order.getOrderCode(),
                order.getEmail(),
                order.getAddress(),
                order.getPostalCode(),
                order.getStatus(),
                order.getStatus().getDescription(),
                order.getOrderedAt(),
                itemResponses,
                totalPrice
        );
    }

    private AdminOrderDetailResponse.OrderItemResponse convertToOrderItemResponse( OrderProduct orderProduct ) {
        int price = orderProduct.getProduct().getPrice();
        int amount = orderProduct.getAmount();

        return new AdminOrderDetailResponse.OrderItemResponse(
                orderProduct.getProduct().getId(),
                orderProduct.getProduct().getName(),
                orderProduct.getProduct().getFilePath(),
                price,
                amount,
                price * amount
        );
    }

    @Transactional
    public void approveCancelOrder( Long orderId ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.CANCEL_REQUESTED) {
            throw new BusinessException(OrderErrorCode.INVALID_CANCEL_REQUEST_STATUS);
        }

        order.cancel();
    }

    @Transactional
    public void rejectCancelOrder( Long orderId ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        order.rejectCancel();
    }

    @Transactional
    public void cancelOrder( Long orderId ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            throw new BusinessException(OrderErrorCode.CANNOT_CANCEL_CONFIRMED_ORDER);
        }

        order.cancel();
    }
}