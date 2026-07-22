package com.springbeans.cafemenumanagement.admin.order.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    public void cancel() {
        if (!this.status.isCancelable()) {
            throw new IllegalStateException("취소할 수 없는 주문 상태입니다. (현재 상태: " + this.status.getDescription() + ")");
        }
        this.status = OrderStatus.CANCELED;
    }

    public void rejectCancel() {
        if (this.status != OrderStatus.CANCEL_REQUESTED) {
            throw new IllegalStateException("취소 요청 상태인 주문만 거절 처리를 할 수 있습니다.");
        }
        // 취소 요청 거절 시 '주문 확정' 상태로 변경
        this.status = OrderStatus.CONFIRMED;
    }
}
