package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.model.order.form.OrderItemForm;
import com.example.order.entity.OrderItem;
import com.example.order.mapper.OrderItemMapper;
import com.example.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    @Override
    @Transactional
    public Boolean insertOrderItemList(List<OrderItem> orderItemList) {
        return this.saveBatch(orderItemList);
    }

    @Override
    public List<OrderItem> getOrderItemList(List<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyList();
        }

        return this.lambdaQuery()
                .in(OrderItem::getOid, orderIds)
                .list();
    }

    @Override
    public OrderItemForm getOrderItemInfo(Long oiid) {
        OrderItemForm orderItemForm=new OrderItemForm();
        BeanUtils.copyProperties(this.getById(oiid),orderItemForm);
        return orderItemForm;
    }

    @Override
    public List<OrderItemForm> getOrderItemListByOrderIds(List<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyList();
        }

        List<OrderItem> orderItemList=this.lambdaQuery()
                .in(OrderItem::getOid, orderIds)
                .list();
        return orderItemList.stream()
                .map(orderItem -> {
                    OrderItemForm form = new OrderItemForm();
                    BeanUtils.copyProperties(orderItem, form);
                    return form;
                })
                .collect(Collectors.toList());
    }
}
