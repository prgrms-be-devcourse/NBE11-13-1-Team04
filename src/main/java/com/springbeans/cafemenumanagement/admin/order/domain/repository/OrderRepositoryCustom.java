package com.springbeans.cafemenumanagement.admin.order.domain.repository;

import com.springbeans.cafemenumanagement.admin.order.domain.entity.Order;
import com.springbeans.cafemenumanagement.admin.order.dto.AdminOrderSearchCondition;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findAllByCondition( AdminOrderSearchCondition condition );
}
