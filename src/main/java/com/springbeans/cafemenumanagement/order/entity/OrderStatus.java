package com.springbeans.cafemenumanagement.order.entity;

public enum OrderStatus {
    ORDERED,            // 주문
    CONFIRMED,          // 확정
    CANCEL_REQUESTED,   // 취소 요청
    CANCELED            // 취소 완료
}