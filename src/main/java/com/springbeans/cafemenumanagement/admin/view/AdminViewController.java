package com.springbeans.cafemenumanagement.admin.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {


    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    // /admin 기본 접속 시 /admin/orders로 리다이렉트
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/admin/orders";
    }

    // 주문 목록 메인 페이지
    @GetMapping("/orders")
    public String orderListPage() {
        return "admin/admin-orders";
    }

    // 주문 상세 페이지
    @GetMapping("/orders/{orderId}")
    public String orderDetailPage(@PathVariable Long orderId ) {
        return "admin/admin-order-detail";
    }
}