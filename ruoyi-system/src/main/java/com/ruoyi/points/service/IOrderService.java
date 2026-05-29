package com.ruoyi.points.service;

import java.util.List;
import com.ruoyi.points.domain.Order;
import com.ruoyi.points.domain.dto.ExchangeDTO;

public interface IOrderService
{
    List<Order> selectOrderList(Order order);
    Order selectOrderById(Long orderId);
    List<Order> selectUserOrders(Long userId, String status);

    /** 兑换商品（核心方法：含分布式锁、库存预减、积分扣减、订单创建） */
    Order exchange(Long userId, ExchangeDTO dto);

    /** 发货（实物） */
    int shipOrder(Order order, String operator);

    /** 关闭订单（退还积分 + 释放库存） */
    int closeOrder(Long orderId, String reason, String operator);

    /** H5 用户确认收货 */
    int confirmReceipt(Long orderId, Long userId);

    /** 修改订单信息 */
    int updateOrder(Order order, String operator);
}
