package com.springbeans.cafemenumanagement.order.service;

import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import com.springbeans.cafemenumanagement.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.order.domain.repository.OrderRepository;
import com.springbeans.cafemenumanagement.order.dto.request.CartOrderRequest;
import com.springbeans.cafemenumanagement.product.entity.Product;
import com.springbeans.cafemenumanagement.product.exception.ProductErrorCode;
import com.springbeans.cafemenumanagement.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CartService cartService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
    }

    // CartOrderRequest / CartItem은 기본 생성자와 @Getter만 있고
    // 세터나 전체 필드 생성자가 없으므로 ReflectionTestUtils로 필드를 채운다.
    private CartOrderRequest createRequest(Long productId, int amount) {
        CartOrderRequest.CartItem item = new CartOrderRequest.CartItem();
        ReflectionTestUtils.setField(item, "productId", productId);
        ReflectionTestUtils.setField(item, "amount", amount);

        CartOrderRequest request = new CartOrderRequest();
        ReflectionTestUtils.setField(request, "email", "test@example.com");
        ReflectionTestUtils.setField(request, "address", "서울시 강남구");
        ReflectionTestUtils.setField(request, "postalCode", "06236");
        ReflectionTestUtils.setField(request, "items", List.of(item));

        return request;
    }

    @Nested
    @DisplayName("장바구니 상품 검증")
    class Check {

        @Test
        @DisplayName("모든 상품이 존재하면 예외 없이 통과한다")
        void check_allProductsExist_doesNotThrow() {
            CartOrderRequest request = createRequest(1L, 2);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            assertThatCode(() -> cartService.check(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("존재하지 않는 상품이 포함되면 PRODUCT_NOT_FOUND 예외를 던진다")
        void check_productNotFound_throwsException() {
            CartOrderRequest request = createRequest(999L, 1);
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.check(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("장바구니 -> 주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("진행 중인 주문이 없으면 새 주문을 생성하고 재고를 차감한다")
        void createOrder_noExistingOrder_createsNewOrderAndDecreasesStock() {
            CartOrderRequest request = createRequest(1L, 2);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(orderRepository.findFirstByEmailAndAddressAndOrderedAtBetween(
                    eq(request.getEmail()), eq(request.getAddress()), any(), any()))
                    .thenReturn(Optional.empty());
            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            cartService.createOrder(request);

            verify(product).decreaseStock(2);
            // orElseGet에서 생성 시 1회 + 아이템 추가 후 마지막 저장 1회
            verify(orderRepository, times(2)).save(any(Order.class));
        }

        @Test
        @DisplayName("진행 중인 주문이 있으면 기존 주문에 아이템을 추가한다")
        void createOrder_existingOrder_addsToExistingOrder() {
            CartOrderRequest request = createRequest(1L, 2);
            Order existingOrder = Order.create(request.getEmail(), request.getAddress(), request.getPostalCode());

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(orderRepository.findFirstByEmailAndAddressAndOrderedAtBetween(
                    eq(request.getEmail()), eq(request.getAddress()), any(), any()))
                    .thenReturn(Optional.of(existingOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

            cartService.createOrder(request);

            verify(orderRepository, times(1)).save(any(Order.class));
            assertThatCode(() -> existingOrder.getOrderProducts()).doesNotThrowAnyException();
            org.assertj.core.api.Assertions.assertThat(existingOrder.getOrderProducts()).hasSize(1);
        }

        @Test
        @DisplayName("존재하지 않는 상품이 포함되면 PRODUCT_NOT_FOUND 예외를 던진다")
        void createOrder_productNotFound_throwsException() {
            CartOrderRequest request = createRequest(999L, 1);
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.createOrder(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }
}
