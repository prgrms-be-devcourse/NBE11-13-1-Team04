package com.springbeans.cafemenumanagement.admin.sales.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springbeans.cafemenumanagement.admin.sales.dto.SalesStatResponse;
import com.springbeans.cafemenumanagement.order.domain.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.springbeans.cafemenumanagement.order.domain.entity.QOrder.order;
import static com.springbeans.cafemenumanagement.order.domain.entity.QOrderProduct.orderProduct;
import static com.springbeans.cafemenumanagement.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class SalesRepositoryCustomImpl implements SalesRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 월별 매출/수량 통계
    @Override
    public List<SalesStatResponse.PeriodSalesStat> getMonthlySales(int year) {
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, '%Y-%m')", order.orderedAt
                                                                 );

        NumberExpression<Long> sumPrice = Expressions.numberTemplate(Long.class, "sum({0} * {1})", product.price, orderProduct.amount);
        NumberExpression<Long> sumAmount = Expressions.numberTemplate(Long.class, "sum({0})", orderProduct.amount);

        return queryFactory
                .select(Projections.constructor(SalesStatResponse.PeriodSalesStat.class,
                                                formattedDate,
                                                sumPrice,
                                                sumAmount
                                               ))
                .from(orderProduct)
                .join(orderProduct.order, order)
                .join(orderProduct.product, product)
                .where(
                        order.status.eq(OrderStatus.CONFIRMED), // ★ ne(CANCELED) -> eq(CONFIRMED) 로 변경
                        order.orderedAt.year().eq(year)
                      )
                .groupBy(formattedDate)
                .orderBy(formattedDate.asc())
                .fetch();
    }

    // 일별 매출/수량 통계
    @Override
    public List<SalesStatResponse.PeriodSalesStat> getDailySales(LocalDate startDate, LocalDate endDate) {
        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, '%Y-%m-%d')", order.orderedAt
                                                                 );

        NumberExpression<Long> sumPrice = Expressions.numberTemplate(Long.class, "sum({0} * {1})", product.price, orderProduct.amount);
        NumberExpression<Long> sumAmount = Expressions.numberTemplate(Long.class, "sum({0})", orderProduct.amount);

        return queryFactory
                .select(Projections.constructor(SalesStatResponse.PeriodSalesStat.class,
                                                formattedDate,
                                                sumPrice,
                                                sumAmount
                                               ))
                .from(orderProduct)
                .join(orderProduct.order, order)
                .join(orderProduct.product, product)
                .where(
                        order.status.eq(OrderStatus.CONFIRMED), // ★ ne(CANCELED) -> eq(CONFIRMED) 로 변경
                        order.orderedAt.goe(startDate.atStartOfDay()),
                        order.orderedAt.loe(endDate.atTime(LocalTime.MAX))
                      )
                .groupBy(formattedDate)
                .orderBy(formattedDate.asc())
                .fetch();
    }

    // 인기 상품 TOP N 통계
    @Override
    public List<SalesStatResponse.TopProductStat> getTopProducts(LocalDate startDate, LocalDate endDate, int limit) {
        NumberExpression<Long> sumPrice = Expressions.numberTemplate(Long.class, "sum({0} * {1})", product.price, orderProduct.amount);
        NumberExpression<Long> sumAmount = Expressions.numberTemplate(Long.class, "sum({0})", orderProduct.amount);

        return queryFactory
                .select(Projections.constructor(SalesStatResponse.TopProductStat.class,
                                                product.name,
                                                sumAmount,
                                                sumPrice
                                               ))
                .from(orderProduct)
                .join(orderProduct.order, order)
                .join(orderProduct.product, product)
                .where(
                        order.status.eq(OrderStatus.CONFIRMED), // ★ ne(CANCELED) -> eq(CONFIRMED) 로 변경
                        order.orderedAt.goe(startDate.atStartOfDay()),
                        order.orderedAt.loe(endDate.atTime(LocalTime.MAX))
                      )
                .groupBy(product.id, product.name)
                .orderBy(sumAmount.desc())
                .limit(limit)
                .fetch();
    }
}