package com.springbeans.cafemenumanagement.order.controller;

import com.springbeans.cafemenumanagement.global.slack.SlackNotificationService;
import com.springbeans.cafemenumanagement.order.dto.request.CartOrderRequest;
import com.springbeans.cafemenumanagement.order.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartApiController.class)
class CartApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private SlackNotificationService slackNotificationService;

    // CartOrderRequest / CartItem은 기본 생성자와 @Getter만 있어
    // ReflectionTestUtils로 필드를 채운 뒤 JsonMapper로 직렬화한다.
    private CartOrderRequest createRequest() {
        CartOrderRequest.CartItem item = new CartOrderRequest.CartItem();
        ReflectionTestUtils.setField(item, "productId", 1L);
        ReflectionTestUtils.setField(item, "amount", 2);

        CartOrderRequest request = new CartOrderRequest();
        ReflectionTestUtils.setField(request, "email", "test@example.com");
        ReflectionTestUtils.setField(request, "address", "서울시 강남구");
        ReflectionTestUtils.setField(request, "postalCode", "06236");
        ReflectionTestUtils.setField(request, "items", List.of(item));

        return request;
    }

    @Test
    @DisplayName("POST /api/cart/check - 장바구니 검증에 성공하면 200과 완료 메시지를 반환한다")
    void check_returnsOk() throws Exception {
        CartOrderRequest request = createRequest();

        mockMvc.perform(post("/api/cart/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("장바구니 확인 완료"));

        verify(cartService).check(any(CartOrderRequest.class));
    }

    @Test
    @DisplayName("POST /api/cart/order - 주문 생성에 성공하면 200과 완료 메시지를 반환한다")
    void order_returnsOk() throws Exception {
        CartOrderRequest request = createRequest();

        mockMvc.perform(post("/api/cart/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("주문 생성 완료"));

        verify(cartService).createOrder(any(CartOrderRequest.class));
    }
}