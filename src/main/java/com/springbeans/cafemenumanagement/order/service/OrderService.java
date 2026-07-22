package com.springbeans.cafemenumanagement.order.service;

import com.example.demo.dto.request.OrderCreateRequest;
import com.example.demo.dto.response.OrderCancelResponse;
import com.example.demo.dto.response.OrderCreateResponse;
import com.example.demo.dto.response.OrderDetailResponse;
import com.example.demo.dto.response.OrderSummaryResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderStatus;
import com.example.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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



        request.items()
                .forEach(item -> {

                    OrderItem orderItem =
                            new OrderItem(
                                    item.productId(),
                                    item.quantity(),
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
    public OrderDetailResponse getOrder(Long orderId) {

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
    public OrderCancelResponse requestCancel(Long orderId) {

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