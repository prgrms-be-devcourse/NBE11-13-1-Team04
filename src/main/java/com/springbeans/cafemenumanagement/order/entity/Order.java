package com.springbeans.cafemenumanagement.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String email;


    private String address;


    @Column(name = "postal_code")
    private String postalCode;


    @Column(name = "order_code")
    private String orderCode;


    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;


    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();


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


    public void addItem(OrderItem item){
        items.add(item);
    }


    public void requestCancel(){
        this.status = OrderStatus.CANCEL_REQUESTED;
    }
}