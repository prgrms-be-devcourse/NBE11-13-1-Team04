package com.springbeans.cafemenumanagement.admin.sales.controller;

import com.springbeans.cafemenumanagement.admin.sales.dto.SalesStatResponse;
import com.springbeans.cafemenumanagement.admin.sales.service.AdminSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/sales")
@RequiredArgsConstructor
public class AdminSalesApiController {

    private final AdminSalesService adminSalesService;

    @GetMapping("/stats")
    public ResponseEntity<SalesStatResponse> getSalesStats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
                                                          ) {
        return ResponseEntity.ok(adminSalesService.getSalesStatistics(year, month));
    }
}