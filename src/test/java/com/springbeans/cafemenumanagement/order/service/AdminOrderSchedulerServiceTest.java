package com.springbeans.cafemenumanagement.order.service;

import com.springbeans.cafemenumanagement.global.slack.SlackNotificationService;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;
import com.springbeans.cafemenumanagement.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminOrderSchedulerServiceTest {

    @InjectMocks
    private AdminOrderSchedulerService adminOrderSchedulerService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SlackNotificationService slackNotificationService;

    @Nested
    @DisplayName("주문 자동 확정 스케줄러 테스트")
    class AutoConfirmOrdersTest {

        @Test
        @DisplayName("성공: 자동 확정된 주문이 1건 이상(예: 5건)인 경우 벌크 업데이트 후 Slack 알림을 발송한다")
        void autoConfirmOrders_WithUpdatedOrders_SendsSlackNotification() {
            // given
            int updatedCount = 5;

            given(orderRepository.bulkConfirmOrders(
                    eq(OrderStatus.ORDERED),
                    eq(OrderStatus.CONFIRMED),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
                                                   )).willReturn(updatedCount);

            // when
            adminOrderSchedulerService.autoConfirmOrders();

            // then
            verify(orderRepository).bulkConfirmOrders(
                    eq(OrderStatus.ORDERED),
                    eq(OrderStatus.CONFIRMED),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
                                                     );
            verify(slackNotificationService).sendBatchConfirmNotification(updatedCount);
        }

        @Test
        @DisplayName("성공: 자동 확정된 주문이 0건인 경우 벌크 업데이트만 수행하고 Slack 알림은 발송하지 않는다")
        void autoConfirmOrders_ZeroUpdatedOrders_DoesNotSendSlackNotification() {
            // given
            int updatedCount = 0;

            given(orderRepository.bulkConfirmOrders(
                    eq(OrderStatus.ORDERED),
                    eq(OrderStatus.CONFIRMED),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
                                                   )).willReturn(updatedCount);

            // when
            adminOrderSchedulerService.autoConfirmOrders();

            // then
            verify(orderRepository).bulkConfirmOrders(
                    eq(OrderStatus.ORDERED),
                    eq(OrderStatus.CONFIRMED),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
                                                     );
            // 0건일 때는 Slack 알림 메서드가 전혀 호출되지 않아야 함
            verify(slackNotificationService, never()).sendBatchConfirmNotification(any(Integer.class));
        }
    }
}