package com.springbeans.cafemenumanagement.order.controller;


import com.example.demo.dto.request.CartOrderRequest;
import com.example.demo.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {


    private final CartService cartService;



    /**
     * 장바구니 상품 검증
     */
    @PostMapping("/check")
    public String check(
            @RequestBody CartOrderRequest request
    ){

        cartService.check(request);

        return "장바구니 확인 완료";
    }



    /**
     * 장바구니 -> 주문 생성
     */
    @PostMapping("/order")
    public String order(
            @RequestBody CartOrderRequest request
    ){

        cartService.createOrder(request);

        return "주문 생성 완료";
    }

}