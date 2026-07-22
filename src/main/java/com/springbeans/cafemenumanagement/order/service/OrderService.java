package com.springbeans.cafemenumanagement.order.service;

import com.springbeans.cafemenumanagement.order.dto.request.OrderCreateRequest;
import com.springbeans.cafemenumanagement.order.dto.response.OrderCancelResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderCreateResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderDetailResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderSummaryResponse;
import com.springbeans.cafemenumanagement.order.entity.Order;
import com.springbeans.cafemenumanagement.order.entity.OrderProduct;
import com.springbeans.cafemenumanagement.order.entity.OrderStatus;
import com.springbeans.cafemenumanagement.order.repository.OrderRepository;
import com.springbeans.cafemenumanagement.product.entity.Product;
import com.springbeans.cafemenumanagement.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
    public OrderCreateResponse createOrder(
            OrderCreateRequest request
                                          ) {


        LocalDateTime start =
                LocalDateTime.now()
                        .toLocalDate()
                        .atStartOfDay();


        LocalDateTime end =
                LocalDateTime.now()
                        .toLocalDate()
                        .atTime(14,0);



        Order order =
                orderRepository
                        .findFirstByEmailAndOrderedAtBetween(
                                request.email(),
                                start,
                                end
                        )
                        .orElseGet(() -> {

                            Order newOrder =
                                    Order.create(
                                            request.email(),
                                            request.address(),
                                            request.postalCode()
                                                );

                            return orderRepository.save(newOrder);
                        });



//        request.items()
//                .forEach(item -> {
//
//                    OrderProduct orderItem =
//                            new OrderProduct(
//                                    item.productId(),
//                                    item.amount(),
//                                    order
//                            );
//
//
//                    order.addItem(orderItem);
//
//                });
        request.items().forEach(item -> {
            // DB에서 실제 Product 엔티티 조회 (존재하지 않는 상품 예외 처리)
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다. ID: " + item.productId()));

            OrderProduct orderItem = new OrderProduct(
                    product,
                    item.amount(),
                    order
            );

            order.addItem(orderItem);
        });



        Order savedOrder =
                orderRepository.save(order);



        return new OrderCreateResponse(
                savedOrder.getId(),
                savedOrder.getOrderCode(),
                savedOrder.getStatus(),
                savedOrder.getOrderedAt()
        );
    }

    /**
     * 주문 목록 조회
     */
    public List<OrderSummaryResponse> getOrders() {

        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderSummaryResponse(
                        order.getId(),
                        order.getEmail(),
                        order.getStatus(),
                        order.getOrderedAt()
                ))
                .toList();
    }

    /**
     * 주문 상세 조회
     */
    public OrderDetailResponse getOrder( Long orderId ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

        return new OrderDetailResponse(
                order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getPostalCode(),
                order.getOrderCode(),
                order.getStatus(),
                order.getOrderedAt()
        );
    }

    /**
     * 주문 취소 요청
     */
    public OrderCancelResponse requestCancel( Long orderId ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "확정된 주문은 취소할 수 없습니다."
            );
        }

        if (order.getStatus() == OrderStatus.CANCEL_REQUESTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 취소 요청된 주문입니다."
            );
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 취소된 주문입니다."
            );
        }

        order.requestCancel();

        orderRepository.save(order);

        return new OrderCancelResponse(
                order.getId(),
                order.getStatus()
        );
    }
}