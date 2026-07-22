package com.springbeans.cafemenumanagement.admin.order.service;

import com.springbeans.cafemenumanagement.admin.order.domain.repository.OrderAdminRepository;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderDetailResponse;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderListResponse;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderSearchCondition;
import com.springbeans.cafemenumanagement.order.entity.Order;
import com.springbeans.cafemenumanagement.order.entity.OrderProduct;
import com.springbeans.cafemenumanagement.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {
    private final OrderAdminRepository orderAdminRepository;

    public List<AdminOrderListResponse> getOrders( AdminOrderSearchCondition condition ) {
        List<Order> orders = orderAdminRepository.findAllByCondition(condition);

        return orders.stream()
                .map(this::convertToAdminOrderListResponse)
                .toList();
    }

    public AdminOrderDetailResponse getOrderDetail( Long orderId ) {
        Order order = orderAdminRepository.findByIdWithProducts(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. ID: " + orderId));

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
        Order order = orderAdminRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. id: " + orderId));

        if (order.getStatus() != OrderStatus.CANCEL_REQUESTED) {
            throw new IllegalArgumentException("취소 요청 상태의 주문만 승인할 수 있습니다.");
        }

        order.cancel();
    }

    @Transactional
    public void rejectCancelOrder(Long orderId) {
        Order order = orderAdminRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. ID: " + orderId));

        order.rejectCancel();
    }

    @Transactional
    public void cancelOrder( Long orderId ) {
        Order order = orderAdminRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. ID: " + orderId));

        if(order.getStatus() == OrderStatus.CONFIRMED) {
            new IllegalArgumentException("확정된 주문은 취소할 수 없습니다.");
        }
        order.cancel();
    }

}
