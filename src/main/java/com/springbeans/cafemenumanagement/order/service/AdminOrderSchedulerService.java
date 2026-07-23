package com.springbeans.cafemenumanagement.order.service;

import com.springbeans.cafemenumanagement.global.slack.SlackNotificationService;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;
import com.springbeans.cafemenumanagement.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderSchedulerService {

    private final OrderRepository orderRepository;
    private final SlackNotificationService slackNotificationService;

    @Scheduled(cron = "0 0 14 * * *", zone = "Asia/Seoul")
    @Transactional
    public void autoConfirmOrders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = now.minusDays(1);
        LocalDateTime endDateTime = now;

        log.info("=== [스케줄러 동작] 주문 자동 확정 처리 시작 (범위: {} ~ {}) ===", startDateTime, endDateTime);

        int updatedCount = orderRepository.bulkConfirmOrders(
                OrderStatus.ORDERED,
                OrderStatus.CONFIRMED,
                now,
                startDateTime,
                endDateTime
                                                            );

        log.info("=== [스케줄러 완료] 총 {}건의 주문이 확정 상태로 변경되었습니다. ===", updatedCount);

        // 확정된 주문이 1건 이상일 때 건수만 알림 발송
        if (updatedCount > 0) {
            slackNotificationService.sendBatchConfirmNotification(updatedCount);
        }
    }
}