package com.ruoyi.points.mapper;

import java.util.List;
import com.ruoyi.points.domain.Coupon;

public interface CouponMapper
{
    List<Coupon> selectCouponList(Coupon coupon);
    Coupon selectCouponById(Long couponId);
    int insertCoupon(Coupon coupon);
    int updateCoupon(Coupon coupon);
    int deleteCouponByIds(Long[] couponIds);
    
    // 发行量控制
    int increaseIssuedCount(Long couponId);
    
    // 关联商品/分类
    int insertCouponGoods(Long couponId, Long[] refIds);
    int deleteCouponGoodsByCouponId(Long couponId);
    List<Long> selectCouponGoodsIds(Long couponId);
}