package com.springbeans.cafemenumanagement.order.controller;

import com.springbeans.cafemenumanagement.order.dto.request.OrderCreateRequest;
import com.springbeans.cafemenumanagement.order.dto.response.OrderCancelResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderCreateResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderDetailResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderSummaryResponse;
import com.springbeans.cafemenumanagement.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreateResponse createOrder(
            @RequestBody OrderCreateRequest request
                                          ) {
        return orderService.createOrder(request);
    }

    /**
     * 주문 목록 조회
     */
    @GetMapping
    public List<OrderSummaryResponse> getOrders() {
        return orderService.getOrders();
    }

    /**
     * 주문 상세 조회
     */
    @GetMapping("/{orderId}")
    public OrderDetailResponse getOrder(
            @PathVariable Long orderId
                                       ) {
        return orderService.getOrder(orderId);
    }

    /**
     * 주문 취소 요청
     */
    @PatchMapping("/{orderId}/cancel")
    public OrderCancelResponse requestCancel(
            @PathVariable Long orderId
                                            ) {
        return orderService.requestCancel(orderId);
    }
}