package com.springbeans.cafemenumanagement.order.service;


import com.example.demo.dto.request.CartOrderRequest;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class CartService {


    private final ProductRepository productRepository;


    /**
     * 상품 존재 여부 및 재고 확인
     */
    public void check(
            CartOrderRequest request
    ){

        request.getItems()
                .forEach(item -> {

                    Product product =
                            productRepository.findById(item.getProductId())
                                    .orElseThrow(() ->
                                            new ResponseStatusException(
                                                    HttpStatus.NOT_FOUND,
                                                    "상품이 존재하지 않습니다. (상품ID: " + item.getProductId() + ")"
                                            ));

                    if (product.getStock() < item.getQuantity()) {

                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "'" + product.getName() + "' 상품의 재고가 부족합니다. 현재 재고: "
                                        + product.getStock() + "개"
                        );
                    }

                    System.out.println(
                            "상품ID : "
                                    + item.getProductId()
                    );

                    System.out.println(
                            "수량 : "
                                    + item.getQuantity()
                    );

                });

    }



    /**
     * 주문 생성
     */
    public void createOrder(
            CartOrderRequest request
    ){

        // 주문 생성 전에도 동일하게 존재/재고 검증
        check(request);

        /*
          여기서

          1. Order 생성

          2. OrderItem 생성

          3. 저장

          진행

        */


        request.getItems()
                .forEach(item -> {

                    System.out.println(
                            "주문 상품 : "
                                    + item.getProductId()
                    );

                });

    }


}