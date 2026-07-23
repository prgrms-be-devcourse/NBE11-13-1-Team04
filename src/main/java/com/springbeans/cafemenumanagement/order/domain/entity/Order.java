package com.springbeans.cafemenumanagement.order.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "postal_code")
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;


    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Builder.Default
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderProduct> orderProducts = new ArrayList<>();


    public static Order create(
            String email,
            String address,
            String postalCode
    ) {

        return Order.builder()
                .email(email)
                .address(address)
                .postalCode(postalCode)
                .orderCode(UUID.randomUUID().toString())
                .status(OrderStatus.ORDERED)
                .orderedAt(LocalDateTime.now())
                .build();
    }


    public void addItem(OrderProduct item){
        orderProducts.add(item);
    }

    public void requestCancel(){
        this.status = OrderStatus.CANCEL_REQUESTED;
    }


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
        this.status = OrderStatus.ORDERED;
    }
}