package com.springbeans.cafemenumanagement.order.domain.repository;

import com.springbeans.cafemenumanagement.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    // ===== 사용자(User) 전용 쿼리 =====

    // 같은 이메일 + 같은 주소 + 당일(기간 내) 주문을 찾아 병합용으로 사용
    Optional<Order> findFirstByEmailAndAddressAndOrderedAtBetween(
            String email,
            String address,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Order> findByEmailOrderByOrderedAtDesc(String email);

    List<Order> findAllByOrderByOrderedAtDesc();


    // ===== 관리자(Admin) 전용 쿼리 =====

    // Fetch Join을 통한 단건 상세 조회 (Order + OrderProduct + Product)
    @Query("select o from Order o " +
            "left join fetch o.orderProducts op " +
            "left join fetch op.product " +
            "where o.id = :orderId")
    Optional<Order> findByIdWithProducts(@Param("orderId") Long orderId);

    // 스케줄러 일괄 자동 확정 벌크 쿼리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Order o " +
            "SET o.status = :targetStatus, " +
            "    o.confirmedAt = :confirmedAt " +
            "WHERE o.status = :sourceStatus " +
            "AND o.orderedAt > :startTime AND o.orderedAt <= :endTime")
    int bulkConfirmOrders(@Param("sourceStatus") OrderStatus sourceStatus,
                          @Param("targetStatus") OrderStatus targetStatus,
                          @Param("confirmedAt") LocalDateTime confirmedAt,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    // 자동 확정 대상 주문 조회
    @Query("select o from Order o " +
           "where o.status = :status " +
           "and o.orderedAt > :startTime and o.orderedAt <= :endTime")
    List<Order> findTargetOrders(@Param("status") OrderStatus status,
                                 @Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime);
}