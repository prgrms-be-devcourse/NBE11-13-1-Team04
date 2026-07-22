package com.springbeans.cafemenumanagement.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "order-list";
    }

    @GetMapping("/orders/new")
    public String createPage() {
        return "order-create";
    }

    @GetMapping("/orders")
    public String listPage() {
        return "order-list";
    }

    @GetMapping("/orders/detail")
    public String detailPage() {
        return "order-detail";
    }
}