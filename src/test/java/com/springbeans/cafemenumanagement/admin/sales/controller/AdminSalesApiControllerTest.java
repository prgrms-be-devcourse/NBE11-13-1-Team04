package com.springbeans.cafemenumanagement.admin.sales.controller;

import com.springbeans.cafemenumanagement.admin.sales.dto.SalesStatResponse;
import com.springbeans.cafemenumanagement.admin.sales.service.AdminSalesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminSalesApiControllerTest {

    @InjectMocks
    private AdminSalesApiController adminSalesApiController;

    @Mock
    private AdminSalesService adminSalesService;

    @Nested
    @DisplayName("GET /api/admin/sales/stats - 매출 통계 조회 API")
    class GetSalesStatsTest {

        @Test
        @DisplayName("성공: year와 month 파라미터가 전달되면 Service를 호출하고 200 OK 응답을 반환한다")
        void getSalesStats_WithParams_Success() {
            // given
            Integer year = 2026;
            Integer month = 5;

            SalesStatResponse.Summary summary = new SalesStatResponse.Summary(150000L, 30L, 50000L, 10L);
            SalesStatResponse mockResponse = new SalesStatResponse(
                    summary,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            given(adminSalesService.getSalesStatistics(year, month)).willReturn(mockResponse);

            // when
            ResponseEntity<SalesStatResponse> responseEntity = adminSalesApiController.getSalesStats(year, month);

            // then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isNotNull();
            assertThat(responseEntity.getBody().summary().currentMonthTotalPrice()).isEqualTo(150000L);
            assertThat(responseEntity.getBody().summary().currentMonthTotalCount()).isEqualTo(30L);

            verify(adminSalesService).getSalesStatistics(year, month);
        }

        @Test
        @DisplayName("성공: 파라미터가 null(미입력)이어도 Service로 null이 잘 전달되고 200 OK 응답을 반환한다")
        void getSalesStats_WithoutParams_Success() {
            // given
            SalesStatResponse.Summary summary = new SalesStatResponse.Summary(0L, 0L, 0L, 0L);
            SalesStatResponse mockResponse = new SalesStatResponse(
                    summary,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            given(adminSalesService.getSalesStatistics(null, null)).willReturn(mockResponse);

            // when
            ResponseEntity<SalesStatResponse> responseEntity = adminSalesApiController.getSalesStats(null, null);

            // then
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isNotNull();
            assertThat(responseEntity.getBody().summary().currentMonthTotalPrice()).isEqualTo(0L);

            verify(adminSalesService).getSalesStatistics(null, null);
        }
    }
}