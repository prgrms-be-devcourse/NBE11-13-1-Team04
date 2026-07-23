package com.springbeans.cafemenumanagement.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbeans.cafemenumanagement.global.slack.SlackNotificationService;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;
import com.springbeans.cafemenumanagement.order.dto.request.OrderCreateRequest;
import com.springbeans.cafemenumanagement.order.dto.response.OrderCancelResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderCreateResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderDetailResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderItemResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderSummaryResponse;
import com.springbeans.cafemenumanagement.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.hasSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderApiController.class)
class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private SlackNotificationService slackNotificationService;

    @Nested
    @DisplayName("POST /api/orders - 주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("정상 요청이면 201과 생성된 주문 정보를 반환한다")
        void createOrder_returnsCreated() throws Exception {
            OrderCreateRequest request = new OrderCreateRequest(
                    "test@example.com",
                    "서울시 강남구",
                    "06236",
                    List.of(new OrderCreateRequest.Item(1L, 2))
            );

            LocalDateTime orderedAt = LocalDateTime.now();
            OrderCreateResponse response = new OrderCreateResponse(
                    1L, "ORDER-CODE-1", OrderStatus.ORDERED, orderedAt
            );

            when(orderService.createOrder(any(OrderCreateRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.orderId").value(1L))
                    .andExpect(jsonPath("$.orderCode").value("ORDER-CODE-1"))
                    .andExpect(jsonPath("$.status").value("ORDERED"));

            verify(orderService).createOrder(any(OrderCreateRequest.class));
        }
    }

    @Nested
    @DisplayName("GET /api/orders - 주문 목록 조회")
    class GetOrders {

        @Test
        @DisplayName("전체 주문 목록을 200과 함께 반환한다")
        void getOrders_returnsOk() throws Exception {
            OrderSummaryResponse summary = new OrderSummaryResponse(
                    1L, "a@test.com", "주소", OrderStatus.ORDERED, LocalDateTime.now()
            );
            when(orderService.getOrders()).thenReturn(List.of(summary));

            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].orderId").value(1L))
                    .andExpect(jsonPath("$[0].email").value("a@test.com"));

            verify(orderService).getOrders();
        }
    }

    @Nested
    @DisplayName("GET /api/orders/mine - 본인 주문 조회")
    class GetMyOrders {

        @Test
        @DisplayName("email 파라미터로 본인 주문만 조회한다")
        void getMyOrders_returnsOk() throws Exception {
            OrderSummaryResponse summary = new OrderSummaryResponse(
                    1L, "me@test.com", "주소", OrderStatus.ORDERED, LocalDateTime.now()
            );
            when(orderService.getOrdersByEmail("me@test.com")).thenReturn(List.of(summary));

            mockMvc.perform(get("/api/orders/mine").param("email", "me@test.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].email").value("me@test.com"));

            verify(orderService).getOrdersByEmail(eq("me@test.com"));
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{orderId} - 주문 상세 조회")
    class GetOrder {

        @Test
        @DisplayName("주문 상세 정보를 200과 함께 반환한다")
        void getOrder_returnsOk() throws Exception {
            OrderItemResponse item = new OrderItemResponse(1L, "아메리카노", 4000, 2, 8000);
            OrderDetailResponse detail = new OrderDetailResponse(
                    1L, "test@example.com", "서울시 강남구", "06236",
                    "ORDER-CODE-1", OrderStatus.ORDERED, LocalDateTime.now(),
                    List.of(item), 8000
            );
            when(orderService.getOrder(1L)).thenReturn(detail);

            mockMvc.perform(get("/api/orders/{orderId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(1L))
                    .andExpect(jsonPath("$.items[0].productName").value("아메리카노"))
                    .andExpect(jsonPath("$.totalPrice").value(8000));

            verify(orderService).getOrder(1L);
        }
    }

    @Nested
    @DisplayName("PATCH /api/orders/{orderId}/cancel - 주문 취소 요청")
    class RequestCancel {

        @Test
        @DisplayName("취소 요청 처리 결과를 200과 함께 반환한다")
        void requestCancel_returnsOk() throws Exception {
            OrderCancelResponse response = new OrderCancelResponse(1L, OrderStatus.CANCEL_REQUESTED);
            when(orderService.requestCancel(1L)).thenReturn(response);

            mockMvc.perform(patch("/api/orders/{orderId}/cancel", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(1L))
                    .andExpect(jsonPath("$.status").value("CANCEL_REQUESTED"));

            verify(orderService).requestCancel(1L);
        }
    }
}