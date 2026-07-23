package com.springbeans.cafemenumanagement.order.service;

import com.springbeans.cafemenumanagement.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;
import com.springbeans.cafemenumanagement.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AdminOrderService adminOrderService;

    @Test
    void 취소요청_주문은_승인할_수_있다() {

        Order order = mock(Order.class);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(order.getStatus())
                .thenReturn(OrderStatus.CANCEL_REQUESTED);

        adminOrderService.approveCancelOrder(1L);

        verify(order).cancel();
    }

    @Test
    void 취소요청이_아닌_주문은_승인할_수_없다() {

        Order order = mock(Order.class);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(order.getStatus())
                .thenReturn(OrderStatus.ORDERED);

        assertThrows(
                IllegalArgumentException.class,
                () -> adminOrderService.approveCancelOrder(1L)
                    );

        verify(order, never()).cancel();
    }

    @Test
    void 주문이_없으면_승인할_수_없다() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> adminOrderService.approveCancelOrder(1L)
                    );
    }

    @Test
    void 취소거절을_할_수_있다() {

        Order order = mock(Order.class);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        adminOrderService.rejectCancelOrder(1L);

        verify(order).rejectCancel();
    }

    @Test
    void 주문이_없으면_취소거절을_할_수_없다() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> adminOrderService.rejectCancelOrder(1L)
                    );
    }

    @Test
    void 주문을_관리자가_취소할_수_있다() {

        Order order = mock(Order.class);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(order.getStatus())
                .thenReturn(OrderStatus.ORDERED);

        adminOrderService.cancelOrder(1L);

        verify(order).cancel();
    }

    @Test
    void 확정된_주문은_관리자가_취소할_수_없다() {

        Order order = mock(Order.class);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(order.getStatus())
                .thenReturn(OrderStatus.CONFIRMED);

        assertThrows(
                IllegalArgumentException.class,
                () -> adminOrderService.cancelOrder(1L)
                    );

        verify(order, never()).cancel();
    }

    @Test
    void 주문이_없으면_관리자_취소를_할_수_없다() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> adminOrderService.cancelOrder(1L)
                    );
    }
}