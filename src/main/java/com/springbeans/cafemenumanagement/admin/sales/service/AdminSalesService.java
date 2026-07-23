package com.springbeans.cafemenumanagement.admin.sales.service;

import com.springbeans.cafemenumanagement.admin.sales.dto.SalesStatResponse;
import com.springbeans.cafemenumanagement.admin.sales.exception.SalesErrorCode;
import com.springbeans.cafemenumanagement.admin.sales.repository.SalesRepositoryCustom;
import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSalesService {

    private final SalesRepositoryCustom salesRepository;

    public SalesStatResponse getSalesStatistics(Integer year, Integer month) {
        // 의도적 에러 코드
//        String testStr = null;
//        testStr.length();
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        // 1. 날짜 범위 유효성 검증 및 YearMonth 생성
        YearMonth targetYearMonth = parseYearMonth(targetYear, targetMonth);

        LocalDate monthStart = targetYearMonth.atDay(1);
        LocalDate monthEnd = targetYearMonth.atEndOfMonth();

        // 2. 기간별 데이터 조회
        List<SalesStatResponse.PeriodSalesStat> monthlyStats = salesRepository.getMonthlySales(targetYear);
        List<SalesStatResponse.PeriodSalesStat> dailyStats = salesRepository.getDailySales(monthStart, monthEnd);
        List<SalesStatResponse.TopProductStat> topProducts = salesRepository.getTopProducts(monthStart, monthEnd, 5);

        // 3. Summary 카드 지표 계산
        long currentMonthPrice = dailyStats.stream().mapToLong(SalesStatResponse.PeriodSalesStat::totalPrice).sum();
        long currentMonthCount = dailyStats.stream().mapToLong(SalesStatResponse.PeriodSalesStat::totalAmount).sum();

        String todayStr = now.toString();
        SalesStatResponse.PeriodSalesStat todayStat = dailyStats.stream()
                .filter(d -> d.period().equals(todayStr))
                .findFirst()
                .orElse(new SalesStatResponse.PeriodSalesStat(todayStr, 0L, 0L));

        SalesStatResponse.Summary summary = new SalesStatResponse.Summary(
                currentMonthPrice,
                currentMonthCount,
                todayStat.totalPrice(),
                todayStat.totalAmount()
        );

        return new SalesStatResponse(summary, monthlyStats, dailyStats, topProducts);
    }

    private YearMonth parseYearMonth(int year, int month) {
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException e) {
            // 잘못된 연도/월 입력 시 BusinessException으로 래핑하여 400 Bad Request 응답[span_0](start_span)[span_0](end_span)[span_1](start_span)[span_1](end_span)
            throw new BusinessException(SalesErrorCode.INVALID_DATE_RANGE, e);
        }
    }
}