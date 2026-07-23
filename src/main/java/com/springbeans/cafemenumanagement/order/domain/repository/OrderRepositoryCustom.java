package com.springbeans.cafemenumanagement.order.domain.repository;

import com.springbeans.cafemenumanagement.order.dto.request.AdminOrderSearchCondition;
import com.springbeans.cafemenumanagement.order.domain.entity.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findAllByCondition( AdminOrderSearchCondition condition );
}
