package com.springbeans.cafemenumanagement.admin.order.dto;

import com.springbeans.cafemenumanagement.order.entity.OrderStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record AdminOrderSearchCondition(
        OrderStatus status,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate
) {

}
