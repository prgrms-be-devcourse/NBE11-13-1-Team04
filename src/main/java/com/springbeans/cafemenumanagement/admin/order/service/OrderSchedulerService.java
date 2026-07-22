package com.springbeans.cafemenumanagement.admin.order.service;

import com.springbeans.cafemenumanagement.admin.order.domain.repository.OrderAdminRepository;
import com.springbeans.cafemenumanagement.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSchedulerService {
    private final OrderAdminRepository orderAdminRepository;

    @Scheduled(cron = "0 0 14 * * *", zone = "Asia/Seoul")
    @Transactional
    public void autoConfirmOrders() {
        LocalDateTime now = LocalDateTime.now();
        // 전날 오후 2시 (now.minusDays(1)로 설정)
        LocalDateTime startDateTime = now.minusDays(1);
        LocalDateTime endDateTime = now;

        log.info("=== [스케줄러 동작] 주문 자동 확정 처리 시작 (범위: {} ~ {}) ===", startDateTime, endDateTime);

        int updatedCount = orderAdminRepository.bulkConfirmOrders(
                OrderStatus.ORDERED,
                OrderStatus.CONFIRMED,
                now,
                startDateTime,
                endDateTime
                                                                 );

        log.info("=== [스케줄러 완료] 총 {}건의 주문이 확정 상태로 변경되었습니다. ===", updatedCount);
    }
}
