package com.springbeans.cafemenumanagement.admin.order.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    ORDERED("미확정"),
    CONFIRMED("주문 확정"),
    CANCEL_REQUESTED("취소 요청"),
    CANCELED("취소 완료");

    private final String description;

    // 예: 관리자가 취소 승인/거절을 할 수 있는 상태인지 확인하는 로직
    public boolean isCancelable() {
        return this == ORDERED || this == CANCEL_REQUESTED;
    }

    // 예: 오후 2시 자동 확정 대상인지 확인하는 로직
    public boolean isConfirmable() {
        return this == ORDERED;
    }
}