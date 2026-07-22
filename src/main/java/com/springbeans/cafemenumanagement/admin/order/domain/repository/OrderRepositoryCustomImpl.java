package com.springbeans.cafemenumanagement.admin.order.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springbeans.cafemenumanagement.admin.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.admin.order.domain.entity.OrderStatus;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.springbeans.cafemenumanagement.admin.order.domain.entity.QOrder.order;
import static com.springbeans.cafemenumanagement.admin.order.domain.entity.QOrderProduct.orderProduct;
import static com.springbeans.cafemenumanagement.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Order> findAllByCondition( AdminOrderSearchCondition condition ) {
        return queryFactory
                .selectFrom(order)
                .leftJoin(order.orderProducts, orderProduct).fetchJoin()
                .leftJoin(orderProduct.product, product).fetchJoin()
                .where(
                        eqStatus(condition.status()),
                        goeStartDate(condition.startDate()),
                        loeEndDate(condition.endDate())
                      )
                .orderBy(order.orderedAt.desc())
                .fetch();
    }

    private BooleanExpression eqStatus( OrderStatus status ) {
        return status != null ? order.status.eq(status) : null;
    }

    private BooleanExpression goeStartDate( LocalDate startDate ) {
        if (startDate == null) {
            return null;
        }
        return order.orderedAt.goe(startDate.atStartOfDay());
    }

    private BooleanExpression loeEndDate( LocalDate endDate ) {
        if (endDate == null) {
            return null;
        }
        return order.orderedAt.loe(endDate.atTime(LocalTime.MAX));
    }
}
