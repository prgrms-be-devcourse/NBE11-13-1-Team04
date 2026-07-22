package com.springbeans.cafemenumanagement.admin.order.domain.repository;

import com.springbeans.cafemenumanagement.order.entity.Order;
import com.springbeans.cafemenumanagement.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderAdminRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    @Query("select o from Order o " +
           "left join fetch o.orderProducts op " +
           "left join fetch op.product " +
           "where o.id = :orderId")
    Optional<Order> findByIdWithProducts( @Param ("orderId") Long orderId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Order o SET o.status = :targetStatus WHERE o.status = :sourceStatus AND o.orderedAt <= :cutoffTime")
    int bulkConfirmOrders(@Param("sourceStatus") OrderStatus sourceStatus,
                          @Param("targetStatus") OrderStatus targetStatus,
                          @Param("cutoffTime") LocalDateTime cutoffTime );

}
