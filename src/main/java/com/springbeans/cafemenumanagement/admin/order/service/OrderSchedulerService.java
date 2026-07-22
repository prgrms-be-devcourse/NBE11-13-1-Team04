package com.springbeans.cafemenumanagement.admin.order.service;

import com.springbeans.cafemenumanagement.admin.order.domain.entity.OrderStatus;
import com.springbeans.cafemenumanagement.admin.order.domain.repository.OrderRepository;
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
    private final OrderRepository orderRepository;

    @Scheduled(cron =  "0 0 14 * * *", zone = "Asia/Seoul")
    @Transactional
    public void autoConfirmOrders() {
        LocalDateTime now = LocalDateTime.now();
        log.info("=== [스케줄러 동작] 당일 주문 자동 확정 처리 시작 ({}) ===", now);
        int updatedCount = orderRepository.bulkConfirmOrders(
                OrderStatus.ORDERED,
                OrderStatus.CONFIRMED,
                now
                                                            );
        log.info("=== [스케줄러 완료] 총 {}건의 주문이 확정 상태로 변경되었습니다. ===", updatedCount);

    }
}
