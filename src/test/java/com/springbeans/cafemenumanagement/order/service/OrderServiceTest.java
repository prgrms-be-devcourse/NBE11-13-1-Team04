package com.springbeans.cafemenumanagement.order.service;

import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import com.springbeans.cafemenumanagement.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderProduct;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;
import com.springbeans.cafemenumanagement.order.domain.repository.OrderRepository;
import com.springbeans.cafemenumanagement.order.dto.request.OrderCreateRequest;
import com.springbeans.cafemenumanagement.order.dto.response.OrderCreateResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderDetailResponse;
import com.springbeans.cafemenumanagement.order.dto.response.OrderSummaryResponse;
import com.springbeans.cafemenumanagement.order.exception.OrderErrorCode;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
        lenient().when(product.getId()).thenReturn(1L);
        lenient().when(product.getName()).thenReturn("아메리카노");
        lenient().when(product.getPrice()).thenReturn(4000);
    }

    private OrderCreateRequest createRequest(Long productId, int amount) {
        return new OrderCreateRequest(
                "test@example.com",
                "서울시 강남구",
                "06236",
                List.of(new OrderCreateRequest.Item(productId, amount))
        );
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("같은 이메일/주소로 진행 중인 주문이 없으면 새 주문을 생성한다")
        void createOrder_noExistingOrder_createsNewOrder() {
            OrderCreateRequest request = createRequest(1L, 2);

            when(orderRepository.findFirstByEmailAndAddressAndOrderedAtBetween(
                    eq(request.email()), eq(request.address()), any(), any()))
                    .thenReturn(Optional.empty());
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            OrderCreateResponse response = orderService.createOrder(request);

            assertThat(response.status()).isEqualTo(OrderStatus.ORDERED);
            verify(product).decreaseStock(2);
            verify(orderRepository, times(2)).save(any(Order.class));
        }

        @Test
        @DisplayName("같은 시간대에 진행 중인 주문이 있으면 기존 주문에 아이템을 추가한다")
        void createOrder_existingOrder_addsToExistingOrder() {
            OrderCreateRequest request = createRequest(1L, 3);
            Order existingOrder = Order.create(request.email(), request.address(), request.postalCode());

            when(orderRepository.findFirstByEmailAndAddressAndOrderedAtBetween(
                    eq(request.email()), eq(request.address()), any(), any()))
                    .thenReturn(Optional.of(existingOrder));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

            orderService.createOrder(request);

            // 기존 주문을 찾은 경우 새로 생성(save)하지 않고 마지막 저장만 1회 수행
            verify(orderRepository, times(1)).save(any(Order.class));
            assertThat(existingOrder.getOrderProducts()).hasSize(1);
        }

        @Test
        @DisplayName("존재하지 않는 상품이면 PRODUCT_NOT_FOUND 예외를 던진다")
        void createOrder_productNotFound_throwsException() {
            OrderCreateRequest request = createRequest(999L, 1);

            when(orderRepository.findFirstByEmailAndAddressAndOrderedAtBetween(
                    any(), any(), any(), any()))
                    .thenReturn(Optional.empty());
            when(orderRepository.save(any(Order.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class GetOrders {

        @Test
        @DisplayName("전체 주문을 최신순으로 조회한다")
        void getOrders_returnsAllOrders() {
            Order order1 = Order.create("a@test.com", "주소1", "12345");
            Order order2 = Order.create("b@test.com", "주소2", "54321");

            when(orderRepository.findAllByOrderByOrderedAtDesc()).thenReturn(List.of(order1, order2));

            List<OrderSummaryResponse> result = orderService.getOrders();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(OrderSummaryResponse::email)
                    .containsExactly("a@test.com", "b@test.com");
        }

        @Test
        @DisplayName("이메일로 본인 주문만 조회한다")
        void getOrdersByEmail_returnsOnlyMatchingOrders() {
            Order order = Order.create("me@test.com", "내 주소", "11111");
            when(orderRepository.findByEmailOrderByOrderedAtDesc("me@test.com"))
                    .thenReturn(List.of(order));

            List<OrderSummaryResponse> result = orderService.getOrdersByEmail("me@test.com");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).email()).isEqualTo("me@test.com");
        }

        @Test
        @DisplayName("주문 상세 조회 시 아이템별 소계와 총액을 계산한다")
        void getOrder_returnsDetailWithCalculatedTotal() {
            Order order = Order.create("me@test.com", "내 주소", "11111");
            OrderProduct orderProduct = new OrderProduct(product, 3, order);
            order.addItem(orderProduct);

            when(orderRepository.findByIdWithProducts(1L)).thenReturn(Optional.of(order));

            OrderDetailResponse response = orderService.getOrder(1L);

            assertThat(response.items()).hasSize(1);
            assertThat(response.items().get(0).subtotal()).isEqualTo(4000 * 3);
            assertThat(response.totalPrice()).isEqualTo(4000 * 3);
        }

        @Test
        @DisplayName("존재하지 않는 주문 상세 조회 시 ORDER_NOT_FOUND 예외를 던진다")
        void getOrder_notFound_throwsException() {
            when(orderRepository.findByIdWithProducts(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrder(999L))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(OrderErrorCode.ORDER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("주문 취소 요청")
    class RequestCancel {

        @Test
        @DisplayName("취소 가능한 상태(ORDERED)면 CANCEL_REQUESTED로 변경된다")
        void requestCancel_cancelableStatus_updatesStatus() {
            Order order = Order.create("me@test.com", "내 주소", "11111");
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            orderService.requestCancel(1L);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL_REQUESTED);
        }

        @Test
        @DisplayName("취소 불가능한 상태(CONFIRMED)면 NOT_CANCELABLE_STATUS 예외를 던진다")
        void requestCancel_notCancelableStatus_throwsException() {
            Order confirmedOrder = Order.builder()
                    .email("me@test.com")
                    .address("내 주소")
                    .postalCode("11111")
                    .orderCode("some-code")
                    .status(OrderStatus.CONFIRMED)
                    .orderedAt(LocalDateTime.now())
                    .build();

            when(orderRepository.findById(1L)).thenReturn(Optional.of(confirmedOrder));

            assertThatThrownBy(() -> orderService.requestCancel(1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(OrderErrorCode.NOT_CANCELABLE_STATUS);
        }

        @Test
        @DisplayName("존재하지 않는 주문이면 ORDER_NOT_FOUND 예외를 던진다")
        void requestCancel_orderNotFound_throwsException() {
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.requestCancel(999L))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(OrderErrorCode.ORDER_NOT_FOUND);
        }
    }
}