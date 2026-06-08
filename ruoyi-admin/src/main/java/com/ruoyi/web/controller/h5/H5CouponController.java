package com.ruoyi.web.controller.h5;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.points.domain.Coupon;
import com.ruoyi.points.domain.UserCoupon;
import com.ruoyi.points.service.ICouponService;

@RestController
@RequestMapping("/h5-api/coupon")
//public class H5CouponController extends BaseController
public class H5CouponController extends H5BaseController
{
    @Autowired
    private ICouponService couponService;

    /** 领券中心：查询可领取的优惠券 */
    @GetMapping("/center")
    public AjaxResult center()
    {
        List<Coupon> list = couponService.selectH5CouponList();
        return AjaxResult.success(list);
    }

    /** 主动领取优惠券 */
    @PostMapping("/receive")
    public AjaxResult receive(Long couponId)
    {
        // 0608getUserId报错，之前不报错
//        Long userId = H5TokenInterceptor.getUserId();
        Long userId = currentUserId();
        couponService.receiveCoupon(couponId, userId);
        return AjaxResult.success("领取成功");
    }

    /** 查询我的优惠券列表 */
    @GetMapping("/my")
    public AjaxResult my(String status)
    {
        // 0608getUserId报错，之前不报错
//        Long userId = H5TokenInterceptor.getUserId();
        Long userId = currentUserId();
        UserCoupon q = new UserCoupon();
        q.setUserId(userId);
        q.setStatus(status); // 0未使用 1已使用 2已过期
        List<UserCoupon> list = couponService.selectUserCouponList(q);
        return AjaxResult.success(list);
    }

    /** 下单前获取可用于当前商品的优惠券 */
    @GetMapping("/available")
    public AjaxResult available(@RequestParam Long goodsId, @RequestParam Integer totalPoints)
    {
        // 0608getUserId报错，之前不报错
//        Long userId = H5TokenInterceptor.getUserId();
        Long userId = currentUserId();
        List<UserCoupon> list = couponService.selectAvailableCoupons(userId, goodsId, totalPoints);
        return AjaxResult.success(list);
    }
}