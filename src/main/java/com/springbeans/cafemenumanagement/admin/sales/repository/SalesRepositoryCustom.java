package com.springbeans.cafemenumanagement.admin.sales.repository;

import com.springbeans.cafemenumanagement.admin.sales.dto.SalesStatResponse;
import java.time.LocalDate;
import java.util.List;

public interface SalesRepositoryCustom {
    List<SalesStatResponse.PeriodSalesStat> getMonthlySales(int year);
    List<SalesStatResponse.PeriodSalesStat> getDailySales(LocalDate startDate, LocalDate endDate);
    List<SalesStatResponse.TopProductStat> getTopProducts(LocalDate startDate, LocalDate endDate, int limit);
}