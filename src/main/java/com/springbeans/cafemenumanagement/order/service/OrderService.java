package com.springbeans.cafemenumanagement.order.service;

import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import com.springbeans.cafemenumanagement.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderProduct;
import com.springbeans.cafemenumanagement.order.domain.repository.OrderRepository;
import com.springbeans.cafemenumanagement.order.dto.request.OrderCreateRequest;
import com.springbeans.cafemenumanagement.order.dto.response.OrderCancelResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderCreateResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderDetailResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderItemResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderSummaryResponse;
import com.springbeans.cafemenumanagement.order.exception.OrderErrorCode;
import com.springbeans.cafemenumanagement.product.entity.Product;
import com.springbeans.cafemenumanagement.product.exception.ProductErrorCode;
import com.springbeans.cafemenumanagement.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest request) {

        LocalDateTime now = LocalDateTime.now();
        LocalTime cutoff = LocalTime.of(14, 0);

        // 매일 오후 2시를 기준으로 하루 주기가 갱신됨 (전날 14:00 ~ 당일 14:00)
        LocalDateTime end = now.toLocalTime().isBefore(cutoff)
                ? now.toLocalDate().atTime(cutoff)
                : now.toLocalDate().plusDays(1).atTime(cutoff);

        LocalDateTime start = end.minusDays(1);

        Order order = orderRepository
                .findFirstByEmailAndAddressAndOrderedAtBetween(
                        request.email(),
                        request.address(),
                        start,
                        end
                                                              )
                .orElseGet(() -> {
                    Order newOrder = Order.create(
                            request.email(),
                            request.address(),
                            request.postalCode()
                                                 );
                    return orderRepository.save(newOrder);
                });

        request.items().forEach(item -> {
            // DB에서 실제 Product 엔티티 조회 (존재하지 않는 상품 예외 처리)
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

            // 재고 차감 (프론트에서 재고 부족 여부를 미리 검증하므로 여기서는 별도 체크 없이 차감만 수행)
            product.decreaseStock(item.amount());

            OrderProduct orderItem = new OrderProduct(
                    product,
                    item.amount(),
                    order
            );

            order.addItem(orderItem);
        });

        Order savedOrder = orderRepository.save(order);

        return new OrderCreateResponse(
                savedOrder.getId(),
                savedOrder.getOrderCode(),
                savedOrder.getStatus(),
                savedOrder.getOrderedAt()
        );
    }

    /**
     * 주문 목록 조회 (전체, 최신순)
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrders() {

        return orderRepository.findAllByOrderByOrderedAtDesc()
                .stream()
                .map(order -> new OrderSummaryResponse(
                        order.getId(),
                        order.getEmail(),
                        order.getAddress(),
                        order.getStatus(),
                        order.getOrderedAt()
                ))
                .toList();
    }

    /**
     * 이메일로 본인 주문 내역만 조회
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrdersByEmail(String email) {

        return orderRepository.findByEmailOrderByOrderedAtDesc(email)
                .stream()
                .map(order -> new OrderSummaryResponse(
                        order.getId(),
                        order.getEmail(),
                        order.getAddress(),
                        order.getStatus(),
                        order.getOrderedAt()
                ))
                .toList();
    }

    /**
     * 주문 상세 조회
     */
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrder(Long orderId) {

        Order order = orderRepository.findByIdWithProducts(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        List<OrderItemResponse> items = order.getOrderProducts()
                .stream()
                .map(orderProduct -> {
                    int price = orderProduct.getProduct().getPrice();
                    int amount = orderProduct.getAmount();
                    return new OrderItemResponse(
                            orderProduct.getProduct().getId(),
                            orderProduct.getProduct().getName(),
                            price,
                            amount,
                            price * amount
                    );
                })
                .toList();

        int totalPrice = items.stream()
                .mapToInt(OrderItemResponse::subtotal)
                .sum();

        return new OrderDetailResponse(
                order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getPostalCode(),
                order.getOrderCode(),
                order.getStatus(),
                order.getOrderedAt(),
                items,
                totalPrice
        );
    }

    /**
     * 주문 취소 요청
     */
    @Transactional
    public OrderCancelResponse requestCancel(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        if (!order.getStatus().isCancelable()) {
            throw new BusinessException(OrderErrorCode.NOT_CANCELABLE_STATUS);
        }

        order.requestCancel();

        return new OrderCancelResponse(
                order.getId(),
                order.getStatus()
        );
    }
}