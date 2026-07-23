package com.springbeans.cafemenumanagement.admin.sales.service;

import com.springbeans.cafemenumanagement.admin.sales.dto.SalesStatResponse;
import com.springbeans.cafemenumanagement.admin.sales.repository.SalesRepositoryCustom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminSalesServiceTest {

    @InjectMocks
    private AdminSalesService adminSalesService;

    @Mock
    private SalesRepositoryCustom salesRepository;

    @Nested
    @DisplayName("매출 통계 조회 테스트")
    class GetSalesStatisticsTest {

        @Test
        @DisplayName("성공: 연/월 파라미터가 명시적으로 주어진 경우 해당 기간의 매출 통계 및 Summary를 정확히 계산한다")
        void getSalesStatistics_WithExplicitYearAndMonth() {
            // given
            int targetYear = 2026;
            int targetMonth = 5;
            LocalDate monthStart = LocalDate.of(2026, 5, 1);
            LocalDate monthEnd = LocalDate.of(2026, 5, 31);

            List<SalesStatResponse.PeriodSalesStat> monthlyStats = List.of(
                    new SalesStatResponse.PeriodSalesStat("2026-05", 150000L, 30L)
                                                                          );

            List<SalesStatResponse.PeriodSalesStat> dailyStats = List.of(
                    new SalesStatResponse.PeriodSalesStat("2026-05-01", 50000L, 10L),
                    new SalesStatResponse.PeriodSalesStat("2026-05-02", 100000L, 20L)
                                                                        );

            List<SalesStatResponse.TopProductStat> topProducts = List.of(
                    new SalesStatResponse.TopProductStat("아메리카노", 15L, 67500L)
                                                                        );

            given(salesRepository.getMonthlySales(targetYear)).willReturn(monthlyStats);
            given(salesRepository.getDailySales(monthStart, monthEnd)).willReturn(dailyStats);
            given(salesRepository.getTopProducts(monthStart, monthEnd, 5)).willReturn(topProducts);

            // when
            SalesStatResponse response = adminSalesService.getSalesStatistics(targetYear, targetMonth);

            // then
            assertThat(response).isNotNull();

            // Summary 계산 검증 (5월 1일 + 5월 2일 합계)
            SalesStatResponse.Summary summary = response.summary();
            assertThat(summary.currentMonthTotalPrice()).isEqualTo(150000L); // 50000 + 100000
            assertThat(summary.currentMonthTotalCount()).isEqualTo(30L);     // 10 + 20

            // 오늘 날짜와 일치하는 일별 통계가 없으므로 todayStat은 0L으로 기본 생성됨
            assertThat(summary.todayTotalPrice()).isEqualTo(0L);
            assertThat(summary.todayTotalCount()).isEqualTo(0L);

            // 리스트 검증
            assertThat(response.monthlyStats()).hasSize(1);
            assertThat(response.dailyStats()).hasSize(2);
            assertThat(response.topProducts()).hasSize(1);

            verify(salesRepository).getMonthlySales(targetYear);
            verify(salesRepository).getDailySales(monthStart, monthEnd);
            verify(salesRepository).getTopProducts(monthStart, monthEnd, 5);
        }

        @Test
        @DisplayName("성공: 연/월 파라미터가 null인 경우 현재 연/월 기준으로 조회하고 당일 매출 지표를 반영한다")
        void getSalesStatistics_WithNullYearAndMonth_IncludesTodayStat() {
            // given
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();

            LocalDate monthStart = YearMonth.of(currentYear, currentMonth).atDay(1);
            LocalDate monthEnd = YearMonth.of(currentYear, currentMonth).atEndOfMonth();
            String todayStr = now.toString();

            // 오늘 매출 데이터 포함
            List<SalesStatResponse.PeriodSalesStat> dailyStats = List.of(
                    new SalesStatResponse.PeriodSalesStat(todayStr, 45000L, 9L)
                                                                        );

            given(salesRepository.getMonthlySales(currentYear)).willReturn(Collections.emptyList());
            given(salesRepository.getDailySales(monthStart, monthEnd)).willReturn(dailyStats);
            given(salesRepository.getTopProducts(monthStart, monthEnd, 5)).willReturn(Collections.emptyList());

            // when
            SalesStatResponse response = adminSalesService.getSalesStatistics(null, null);

            // then
            assertThat(response).isNotNull();

            // Summary 검증 (당일 매출 반영 확인)
            SalesStatResponse.Summary summary = response.summary();
            assertThat(summary.currentMonthTotalPrice()).isEqualTo(45000L);
            assertThat(summary.currentMonthTotalCount()).isEqualTo(9L);
            assertThat(summary.todayTotalPrice()).isEqualTo(45000L);
            assertThat(summary.todayTotalCount()).isEqualTo(9L);

            verify(salesRepository).getMonthlySales(currentYear);
            verify(salesRepository).getDailySales(monthStart, monthEnd);
            verify(salesRepository).getTopProducts(monthStart, monthEnd, 5);
        }

        @Test
        @DisplayName("성공: 조회된 일별 매출 데이터가 비어 있을 경우 Summary 지표가 모두 0으로 계산된다")
        void getSalesStatistics_EmptyDailyStats() {
            // given
            int targetYear = 2026;
            int targetMonth = 1;
            LocalDate monthStart = LocalDate.of(2026, 1, 1);
            LocalDate monthEnd = LocalDate.of(2026, 1, 31);

            given(salesRepository.getMonthlySales(targetYear)).willReturn(Collections.emptyList());
            given(salesRepository.getDailySales(monthStart, monthEnd)).willReturn(Collections.emptyList());
            given(salesRepository.getTopProducts(monthStart, monthEnd, 5)).willReturn(Collections.emptyList());

            // when
            SalesStatResponse response = adminSalesService.getSalesStatistics(targetYear, targetMonth);

            // then
            assertThat(response).isNotNull();

            SalesStatResponse.Summary summary = response.summary();
            assertThat(summary.currentMonthTotalPrice()).isEqualTo(0L);
            assertThat(summary.currentMonthTotalCount()).isEqualTo(0L);
            assertThat(summary.todayTotalPrice()).isEqualTo(0L);
            assertThat(summary.todayTotalCount()).isEqualTo(0L);
        }
    }
}