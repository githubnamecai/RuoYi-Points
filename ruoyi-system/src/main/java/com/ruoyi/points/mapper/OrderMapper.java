package com.ruoyi.points.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.points.domain.Order;

/**
 * 订单 Mapper
 */
public interface OrderMapper
{
    /** 管理端订单列表（支持状态/手机号/订单号/商品名等多条件） */
    List<Order> selectOrderList(Order order);

    Order selectOrderById(Long orderId);

    Order selectOrderByNo(String orderNo);

    /** H5 用户订单（按状态） */
    List<Order> selectUserOrders(@Param("userId") Long userId, @Param("status") String status);

    int insertOrder(Order order);

    int updateOrder(Order order);

    /** 发货 */
    int shipOrder(Order order);

    /** 关闭订单 */
    int closeOrder(@Param("orderId") Long orderId, @Param("reason") String reason);

    /** 确认收货（仅 H5 用户在已发货状态下使用） */
    int confirmReceipt(@Param("orderId") Long orderId, @Param("userId") Long userId);
}
