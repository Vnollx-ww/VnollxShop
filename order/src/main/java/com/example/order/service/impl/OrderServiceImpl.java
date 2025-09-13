package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.BusinessException;
import com.example.common.model.cart.dto.DeleteCardItemByShopDTO;
import com.example.common.model.cart.dto.DeleteCardItemDTO;
import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.common.model.order.dto.DeleteOrderDTO;
import com.example.common.model.order.form.OrderForm;
import com.example.common.model.order.form.OrderItemForm;
import com.example.common.model.order.vo.OrderItemVO;
import com.example.common.model.order.vo.OrderVO;
import com.example.common.model.product.dto.StockDeductDTO;
import com.example.common.model.user.dto.UpdateBalanceDTO;
import com.example.common.result.Result;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.feign.CardFeignClient;
import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderItemService;
import com.example.order.feign.ProductFeignClient;
import com.example.order.feign.UserFeignClient;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Order> implements OrderService {
    private final UserFeignClient userFeignClient;
    private final ProductFeignClient productFeignClient;
    private final CardFeignClient cardFeignClient;
    private final OrderItemService orderItemService;
    @Override
    @Transactional
    public void createOrder(CreateOrderDTO dto,Long uid) {

        // 开始生成订单

        //从购物车删除
        List<Long> pidList = dto.getItems().stream()
                .map(orderItemDTO -> Long.parseLong(orderItemDTO.getPid()))
                .collect(Collectors.toList());

        Result<Void> cardResult=cardFeignClient.deleteCardItemByShop(new DeleteCardItemByShopDTO(pidList,uid));
        if (cardResult.getCode()!=200){
            throw new BusinessException(cardResult.getMessage());
        }

        //扣减用户余额
        Result<Void> balanceResult=userFeignClient.updateBalance(new UpdateBalanceDTO(uid,dto.getTotalCost()));
        if (balanceResult.getCode()!=200){
            throw new BusinessException(balanceResult.getMessage());
        }

        //扣减商品库存
        List<StockDeductDTO> deductList = dto.getItems().stream()
                .map(orderItemDTO -> new StockDeductDTO(
                        Long.parseLong(orderItemDTO.getPid()),
                        orderItemDTO.getNumber()
                ))
                .collect(Collectors.toList());
        Result<Void> stockResult=productFeignClient.deductStock(deductList);
        if (stockResult.getCode()!=200){
            throw new BusinessException(stockResult.getMessage());
        }

        //生成订单
        Order order=new Order(dto,uid);
        this.save(order);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid)
                .orderByDesc("create_time") // 按创建时间降序
                .last("LIMIT 1"); // 只取一条

        Order lastOrder = this.getOne(wrapper);
        List<OrderItem> orderItemList = dto.getItems().stream()
                .map(itemDTO -> new OrderItem(itemDTO, lastOrder.getId()))
                .toList();

        orderItemService.insertOrderItemList(orderItemList);
    }

    @Override
    public void deleteOrder(DeleteOrderDTO dto) {
        this.baseMapper.deleteById(dto.getOid());
    }

    @Override
    public List<OrderVO> getOrderList(Long uid) {
        QueryWrapper<Order> orderQueryWrapper=new QueryWrapper<>();
        orderQueryWrapper.eq("uid",uid);
        List<Order> orderList=this.list(orderQueryWrapper);
        if (CollectionUtils.isEmpty(orderList)) {
            return null;
        }

        // 2. 批量获取所有订单ID
        List<Long> orderIds = orderList.stream()
                .map(Order::getId)
                .toList();

        Map<Long, List<OrderItem>> orderItemsMap = orderItemService.getOrderItemList(orderIds)
                .stream()
                .collect(Collectors.groupingBy(OrderItem::getOid));


        orderList.forEach(order ->
                order.setOrderItemList(
                        orderItemsMap.getOrDefault(order.getId(), Collections.emptyList())
                )
        );
        return orderList.stream()
                .map(order -> {
                    OrderForm orderForm = new OrderForm();
                    BeanUtils.copyProperties(order, orderForm);
                    OrderVO orderVO = new OrderVO(orderForm);

                    orderVO.setOrderItemVOList(
                            order.getOrderItemList().stream()
                                    .map(orderItem -> {
                                        OrderItemForm itemForm = new OrderItemForm();
                                        BeanUtils.copyProperties(orderItem, itemForm);
                                        return new OrderItemVO(itemForm);
                                    })
                                    .collect(Collectors.toList())
                    );

                    return orderVO;
                })
                .collect(Collectors.toList());
    }

}
