package com.ruoyi.web.controller.points;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.points.domain.Coupon;
import com.ruoyi.points.domain.UserCoupon;
import com.ruoyi.points.service.ICouponService;
import com.ruoyi.points.service.IUserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points/usercoupon")
public class UserCouponController extends BaseController
{
    @Autowired
    private IUserCouponService usercouponService;

    @PreAuthorize("@ss.hasPermi('points:usercoupon:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserCoupon userCoupon)
    {
        startPage();
        List<UserCoupon> list = usercouponService.selectUserCouponList(userCoupon);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('points:usercoupon:query')")
    @GetMapping(value = "/{userCouponId}")
    public AjaxResult getInfo(@PathVariable("userCouponId") Long userCouponId)
    {
        return success(usercouponService.selectUserCouponById(userCouponId));
    }

    @PreAuthorize("@ss.hasPermi('points:usercoupon:add')")
    @Log(title = "优惠券用户领取记录新增管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserCoupon UserCoupon)
    {

        return toAjax(usercouponService.insertUserCoupon(UserCoupon));
    }

    @PreAuthorize("@ss.hasPermi('points:usercoupon:edit')")
    @Log(title = "优惠券用户领取记录编辑管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserCoupon UserCoupon)
    {
        return toAjax(usercouponService.updateUserCoupon(UserCoupon));
    }

    @PreAuthorize("@ss.hasPermi('points:usercoupon:receive')")
    @Log(title = "优惠券用户判断是否领取管理")
    @DeleteMapping("/receive/{couponId}")
    public AjaxResult receive(@PathVariable Long userId, @PathVariable Long couponId)
    {
        return toAjax(usercouponService.countUserReceive(userId,couponId));
    }

    @PreAuthorize("@ss.hasPermi('points:usercoupon:isstatus')")
    @Log(title = "更新状态", businessType = BusinessType.UPDATE)
    @PostMapping("/setstatus/{userCouponId}")
    public AjaxResult isstatus(@PathVariable Long userCouponId, @RequestBody String status,@RequestBody Long orderId )
    {
        return toAjax(usercouponService.updateStatus(userCouponId,status,orderId));
    }


}