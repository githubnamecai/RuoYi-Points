package com.ruoyi.web.controller.h5;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.domain.Order;
import com.ruoyi.points.domain.PointsDetail;
import com.ruoyi.points.domain.UserAddress;
import com.ruoyi.points.service.IH5UserService;
import com.ruoyi.points.service.IOrderService;
import com.ruoyi.points.service.IPointsDetailService;
import com.ruoyi.points.service.IUserAddressService;

/**
 * H5 用户中心：个人信息、积分明细、订单、地址
 */
@RestController
@RequestMapping("/h5-api/user")
public class H5UserCenterController extends H5BaseController
{
    @Autowired private IH5UserService h5UserService;
    @Autowired private IPointsDetailService pointsDetailService;
    @Autowired private IOrderService orderService;
    @Autowired private IUserAddressService addressService;

    /** 当前用户信息 */
    @GetMapping("/info")
    public AjaxResult info()
    {
        return success(h5UserService.selectUserById(currentUserId()));
    }

    /** 更新昵称 / 头像 */
    @PutMapping("/profile")
    public AjaxResult profile(@RequestBody H5User user)
    {
        user.setUserId(currentUserId());
        // 仅允许更新少量字段
        H5User u = new H5User();
        u.setUserId(currentUserId());
        u.setNickname(user.getNickname());
        u.setName(user.getName());
        u.setAvatar(user.getAvatar());
        return toAjax(h5UserService.updateUser(u));
    }

    /** 更新密码 */
    @PutMapping("/resetpwd")
    public AjaxResult resetpwd(@RequestBody H5User user)
    {
        user.setUserId(currentUserId());
        // 仅允许更新少量字段
        H5User u = new H5User();
        u.setUserId(currentUserId());
//        u.setNickname(user.getNickname());
//        u.setName(user.getName());
//        u.setAvatar(user.getAvatar());
        // H5调用方法更新密码
        u.setPassword(user.getPassword());
        return toAjax(h5UserService.updateUser(u));
    }

    /** 我的积分明细（分页） */
    @GetMapping("/points/detail")
    public TableDataInfo pointsDetail()
    {
        startPage();
        return getDataTable(pointsDetailService.selectUserDetailList(currentUserId()));
    }

    /** 我的订单（按状态） */
    @GetMapping("/orders")
    public TableDataInfo orders(@RequestParam(required = false) String status)
    {
        startPage();
        List<Order> list = orderService.selectUserOrders(currentUserId(), status);
        return getDataTable(list);
    }

    /** 订单详情 */
    @GetMapping("/orders/{orderId}")
    public AjaxResult orderDetail(@PathVariable Long orderId)
    {
        Order o = orderService.selectOrderById(orderId);
        if (o == null || !o.getUserId().equals(currentUserId())) return error("订单不存在");
        return success(o);
    }

    /** 确认收货 */
    @PutMapping("/orders/{orderId}/confirm")
    public AjaxResult confirmReceipt(@PathVariable Long orderId)
    {
        return toAjax(orderService.confirmReceipt(orderId, currentUserId()));
    }

    // ============ 收货地址 ============
    @GetMapping("/addresses")
    public AjaxResult addresses()
    {
        return success(addressService.listByUser(currentUserId()));
    }

    @PostMapping("/addresses")
    public AjaxResult addAddress(@Validated @RequestBody UserAddress address)
    {
        address.setUserId(currentUserId());
        int rows = addressService.add(address);
        if (rows > 0)
        {
            // 返回新插入的地址，前端可拿到 addressId 用于后续操作（如设默认）
            return AjaxResult.success(address);
        }
        return AjaxResult.error();
    }

    @PutMapping("/addresses")
    public AjaxResult updateAddress(@Validated @RequestBody UserAddress address)
    {
        address.setUserId(currentUserId());
        return toAjax(addressService.update(address));
    }

    @DeleteMapping("/addresses/{addressId}")
    public AjaxResult deleteAddress(@PathVariable Long addressId)
    {
        return toAjax(addressService.delete(addressId, currentUserId()));
    }

    @PutMapping("/addresses/{addressId}/default")
    public AjaxResult setDefault(@PathVariable Long addressId)
    {
        return toAjax(addressService.setDefault(addressId, currentUserId()));
    }
}
