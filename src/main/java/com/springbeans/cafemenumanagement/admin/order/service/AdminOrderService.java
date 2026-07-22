package com.springbeans.cafemenumanagement.admin.order.service;

import com.springbeans.cafemenumanagement.admin.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.admin.order.domain.entity.OrderProduct;
import com.springbeans.cafemenumanagement.admin.order.domain.entity.OrderStatus;
import com.springbeans.cafemenumanagement.admin.order.domain.repository.OrderRepository;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderDetailResponse;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderListResponse;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderSearchCondition;
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. ID: " + orderId));

        return convertToAdminOrderDetailResponse(order);
    }


    private AdminOrderListResponse convertToAdminOrderListResponse( Order order ) {
        int totalAmount = order.getOrderProducts().stream()
                .mapToInt(OrderProduct::getAmount).sum();

        int totalPrice = order.getOrderProducts().stream()
                .mapToInt(op -> op.getProduct().getPrice() * op.getAmount()).sum();

        return new AdminOrderListResponse(
                order.getId(),
                order.getOrderCode(),
                order.getEmail(),
                order.getStatus(),
                order.getStatus().getDescription(),
                order.getOrderedAt(),
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. id: " + orderId));

        if (order.getStatus() != OrderStatus.CANCEL_REQUESTED) {
            throw new IllegalArgumentException("취소 요청 상태의 주문만 승인할 수 있습니다.");
        }

        order.cancel();
    }

    @Transactional
    public void rejectCancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. ID: " + orderId));

        order.rejectCancel();
    }

    @Transactional
    public void cancelOrder( Long orderId ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. ID: " + orderId));

        if(order.getStatus() == OrderStatus.CONFIRMED) {
            new IllegalArgumentException("확정된 주문은 취소할 수 없습니다.");
        }
        order.cancel();
    }

}
