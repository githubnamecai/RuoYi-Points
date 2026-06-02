package com.ruoyi.web.controller.points;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.points.domain.Coupon;
import com.ruoyi.points.domain.UserCoupon;
import com.ruoyi.points.service.ICouponService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/points/coupon")
public class CouponController extends BaseController
{
    @Autowired
    private ICouponService couponService;

    @PreAuthorize("@ss.hasPermi('points:coupon:list')")
    @GetMapping("/list")
    public TableDataInfo list(Coupon coupon)
    {
        startPage();
        List<Coupon> list = couponService.selectCouponList(coupon);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('points:coupon:query')")
    @GetMapping(value = "/{couponId}")
    public AjaxResult getInfo(@PathVariable("couponId") Long couponId)
    {
        return success(couponService.selectCouponById(couponId));
    }

    @PreAuthorize("@ss.hasPermi('points:coupon:add')")
    @Log(title = "优惠券管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Coupon coupon)
    {
        coupon.setCreateBy(getUsername());
        return toAjax(couponService.insertCoupon(coupon));
    }

    @PreAuthorize("@ss.hasPermi('points:coupon:edit')")
    @Log(title = "优惠券管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Coupon coupon)
    {
        coupon.setUpdateBy(getUsername());
        return toAjax(couponService.updateCoupon(coupon));
    }

    @PreAuthorize("@ss.hasPermi('points:coupon:remove')")
    @Log(title = "优惠券管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{couponIds}")
    public AjaxResult remove(@PathVariable Long[] couponIds)
    {
        return toAjax(couponService.deleteCouponByIds(couponIds));
    }

    @PreAuthorize("@ss.hasPermi('points:coupon:issue')")
    @Log(title = "发放优惠券", businessType = BusinessType.INSERT)
    @PostMapping("/issue/{couponId}")
    public AjaxResult issue(@PathVariable Long couponId, @RequestBody Long[] userIds)
    {
        int count = couponService.issueCoupon(couponId, userIds, getUsername());
        return success("成功发放 " + count + " 张优惠券");
    }

    @PreAuthorize("@ss.hasPermi('points:coupon:record')")
    @GetMapping("/record/list")
    public TableDataInfo recordList(UserCoupon userCoupon)
    {
        startPage();
        List<UserCoupon> list = couponService.selectUserCouponList(userCoupon);
        return getDataTable(list);
    }
}