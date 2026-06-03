package com.ruoyi.points.service;

import com.ruoyi.points.domain.UserAddress;
import com.ruoyi.points.domain.UserCoupon;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUserCouponService
{

    List<UserCoupon> selectUserCouponList(UserCoupon userCoupon);
    UserCoupon selectUserCouponById(Long userCouponId);
    int insertUserCoupon(UserCoupon userCoupon);
    int updateUserCoupon(UserCoupon userCoupon);

    int countUserReceive(@Param("userId") Long userId, @Param("couponId") Long couponId);

    // 更新状态（使用或过期）
    int updateStatus(@Param("userCouponId") Long userCouponId, @Param("status") String status, @Param("orderId") Long orderId);
}
