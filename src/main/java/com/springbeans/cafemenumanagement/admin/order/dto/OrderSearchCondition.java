package com.springbeans.cafemenumanagement.admin.order.dto;

import java.time.LocalDate;

public record OrderSearchCondition(String status, LocalDate startDate, LocalDate endDate) {
}
