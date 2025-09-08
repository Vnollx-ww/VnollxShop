package com.example.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.BusinessException;
import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.common.model.order.dto.DeleteOrderDTO;
import com.example.common.model.order.form.OrderForm;
import com.example.common.model.order.form.OrderItemForm;
import com.example.common.model.order.vo.OrderItemVO;
import com.example.common.model.order.vo.OrderVO;
import com.example.common.model.product.form.ProductForm;
import com.example.common.model.user.dto.UpdateBalanceDTO;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderItemService;
import com.example.product.entity.Product;
import com.example.order.feign.ProductFeignClient;
import com.example.order.feign.UserFeignClient;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper,Order> implements OrderService {
    private final UserFeignClient userFeignClient;
    private final ProductFeignClient productFeignClient;
    private final OrderItemService orderItemService;
    @Override
    public void createOrder(CreateOrderDTO dto,Long uid) {
        // 检查余额
        if (userFeignClient.getBalance(uid) < dto.getTotalCost()) {
            throw new BusinessException("余额不足，请充值");
        }

        // 检查库存
        List<Long> idList = dto.getItems().stream()
                .map(orderItemDTO -> Long.parseLong(orderItemDTO.getPid()))
                .collect(Collectors.toList());

        List<ProductForm> productFormList=productFeignClient.getProductList(idList);
        List<Product> productList=productFormList.stream()
                .map(productForm -> {
                    Product product = new Product();
                    BeanUtils.copyProperties(productForm, product);
                    return product;
                })
                .toList();

        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        dto.getItems().forEach(orderItemDTO -> {
            Long pid=Long.parseLong(orderItemDTO.getPid());
            Product product = productMap.get(pid);
            if (product == null) {
                throw new BusinessException("商品ID " + pid + " 不存在");
            }
            if (product.getStock() < orderItemDTO.getNumber()) {
                throw new BusinessException("商品" + orderItemDTO.getProductName() + "库存不够");
            }
        });

        // 开始生成订单
        userFeignClient.updateBalance(new UpdateBalanceDTO(uid,dto.getTotalCost()));//扣减用户余额

        List<Pair<Long, Long>> deductPairs = dto.getItems().stream()
                .map(orderItemDTO -> Pair.of(
                        Long.parseLong(orderItemDTO.getPid()),
                        orderItemDTO.getNumber()
                ))
                .collect(Collectors.toList());

        Boolean ok=productFeignClient.deductStock(deductPairs);//扣减商品库存
        if (!ok){
            throw new BusinessException("扣减商品库存失败");
        }
        Order order=new Order(dto);
        ok=this.save(order);
        if (!ok){
            throw new BusinessException("创建订单失败");
        }
        List<OrderItem> orderItemList = dto.getItems().stream()
                .map(itemDTO -> new OrderItem(itemDTO,order.getId()))
                .toList();

        ok=orderItemService.insertOrderItemList(orderItemList);
        if (!ok){
            throw new BusinessException("创建订单项失败");
        }
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
