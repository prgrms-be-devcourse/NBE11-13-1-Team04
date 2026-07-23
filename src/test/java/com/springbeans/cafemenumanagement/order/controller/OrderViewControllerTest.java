package com.springbeans.cafemenumanagement.order.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.springbeans.cafemenumanagement.global.slack.SlackNotificationService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderViewController.class)
class OrderViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SlackNotificationService slackNotificationService;

    @Test
    @DisplayName("/ 요청 시 order-create 뷰를 반환한다")
    void home_returnsOrderCreateView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-create"));
    }

    @Test
    @DisplayName("/orders/new 요청 시 order-create 뷰를 반환한다")
    void createPage_returnsOrderCreateView() throws Exception {
        mockMvc.perform(get("/orders/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-create"));
    }

    @Test
    @DisplayName("/orders 요청 시 order-list 뷰를 반환한다")
    void listPage_returnsOrderListView() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-list"));
    }

    @Test
    @DisplayName("/orders/{orderId} 요청 시 order-detail 뷰를 반환한다")
    void detailPage_returnsOrderDetailView() throws Exception {
        mockMvc.perform(get("/orders/{orderId}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("order-detail"));
    }
}