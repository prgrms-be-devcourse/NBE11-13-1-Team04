package com.springbeans.cafemenumanagement.order.service;

import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import com.springbeans.cafemenumanagement.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderProduct;
import com.springbeans.cafemenumanagement.order.domain.repository.OrderRepository;
import com.springbeans.cafemenumanagement.order.dto.request.CartOrderRequest;
import com.springbeans.cafemenumanagement.product.entity.Product;
import com.springbeans.cafemenumanagement.product.exception.ProductErrorCode; // 👈 기존에 만드신 ProductErrorCode 사용
import com.springbeans.cafemenumanagement.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    /**
     * 상품 존재 여부 확인
     * (재고 부족 여부는 프론트에서 처리하기로 하여 여기서는 체크하지 않음)
     */
    public void check(CartOrderRequest request) {
        request.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
        });
    }

    /**
     * 장바구니 -> 주문 생성
     */
    @Transactional
    public void createOrder(CartOrderRequest request) {

        // 상품 존재 여부만 먼저 확인
        check(request);

        LocalDateTime now = LocalDateTime.now();
        LocalTime cutoff = LocalTime.of(14, 0);

        // 매일 오후 2시를 기준으로 하루 주기가 갱신됨 (전날 14:00 ~ 당일 14:00)
        LocalDateTime end = now.toLocalTime().isBefore(cutoff)
                ? now.toLocalDate().atTime(cutoff)
                : now.toLocalDate().plusDays(1).atTime(cutoff);

        LocalDateTime start = end.minusDays(1);

        Order order = orderRepository
                .findFirstByEmailAndAddressAndOrderedAtBetween(
                        request.getEmail(),
                        request.getAddress(),
                        start,
                        end
                                                              )
                .orElseGet(() -> {
                    Order newOrder = Order.create(
                            request.getEmail(),
                            request.getAddress(),
                            request.getPostalCode()
                                                 );
                    return orderRepository.save(newOrder);
                });

        request.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

            // 재고 차감 (영속 상태 엔티티라 트랜잭션 커밋 시 자동 반영됨)
            product.decreaseStock(item.getAmount());

            OrderProduct orderProduct = new OrderProduct(
                    product,
                    item.getAmount(),
                    order
            );

            order.addItem(orderProduct);
        });

        orderRepository.save(order);
    }
}