package com.ruoyi.points.service;

import java.util.List;
import com.ruoyi.points.domain.Coupon;
import com.ruoyi.points.domain.UserCoupon;

public interface ICouponService
{
    List<Coupon> selectCouponList(Coupon coupon);
    Coupon selectCouponById(Long couponId);
    int insertCoupon(Coupon coupon);
    int updateCoupon(Coupon coupon);
    int deleteCouponByIds(Long[] couponIds);
    
    /** H5查询可领取的优惠券 */
    List<Coupon> selectH5CouponList();
    
    /** 发放优惠券（后台操作） */
    int issueCoupon(Long couponId, Long[] userIds, String operator);
    
    /** H5用户主动领取优惠券 */
    UserCoupon receiveCoupon(Long couponId, Long userId);
    
    /** 查询用户的优惠券记录 */
    List<UserCoupon> selectUserCouponList(UserCoupon userCoupon);
    
    /** 获取用户可用于某商品的优惠券 */
    List<UserCoupon> selectAvailableCoupons(Long userId, Long goodsId, Integer totalPoints);
}