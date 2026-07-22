package com.springbeans.cafemenumanagement.admin.order.controller;

import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderDetailResponse;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderListResponse;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderSearchCondition;
import com.springbeans.cafemenumanagement.admin.order.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderApiController {

    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    public ResponseEntity<List<AdminOrderListResponse>> getOrders(
            @ModelAttribute AdminOrderSearchCondition condition
                                                                 ) {
        try {
            List<AdminOrderListResponse> response = adminOrderService.getOrders(condition);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<AdminOrderDetailResponse> getOrderDetail(
            @PathVariable Long orderId
                                                                  ) {
        AdminOrderDetailResponse response = adminOrderService.getOrderDetail(orderId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/cancel-approve")
    public ResponseEntity<Void> approveCancel( @PathVariable Long orderId ) {
        adminOrderService.approveCancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{orderId}/cancel-reject")
    public ResponseEntity<Void> rejectCancel( @PathVariable Long orderId ) {
        adminOrderService.rejectCancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder( @PathVariable Long orderId ) {
        adminOrderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
