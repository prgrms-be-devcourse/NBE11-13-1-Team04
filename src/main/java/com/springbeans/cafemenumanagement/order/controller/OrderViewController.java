package com.springbeans.cafemenumanagement.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrderViewController {

    @GetMapping("/")
    public String home() {
        return "order-create";
    }

    @GetMapping("/orders/new")
    public String createPage() {
        return "order-create";
    }

    @GetMapping("/orders")
    public String listPage() {
        return "order-list";
    }

    @GetMapping("/orders/{orderId}")
    public String detailPage( @PathVariable Long orderId ) {
        return "order-detail";
    }

}