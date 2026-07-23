package com.springbeans.cafemenumanagement.admin.sales.dto;

import java.util.List;

public record SalesStatResponse(
        Summary summary,
        List<PeriodSalesStat> monthlyStats,
        List<PeriodSalesStat> dailyStats,
        List<TopProductStat> topProducts
) {
    // 핵심 요약 지표 (카드형 UI용)
    public record Summary(
            long currentMonthTotalPrice,
            long currentMonthTotalCount,
            long todayTotalPrice,
            long todayTotalCount
    ) {}

    // 기간별 통계 (월별/일별)
    public record PeriodSalesStat(
            String period,
            long totalPrice,
            long totalAmount
    ) {}

    // 인기 상품 통계
    public record TopProductStat(
            String productName,
            long totalAmount,
            long totalPrice
    ) {}
}