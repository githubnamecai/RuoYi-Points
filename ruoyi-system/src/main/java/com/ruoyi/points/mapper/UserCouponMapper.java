package com.ruoyi.points.mapper;

import java.util.List;
import com.ruoyi.points.domain.UserCoupon;
import org.apache.ibatis.annotations.Param;

public interface UserCouponMapper
{
    List<UserCoupon> selectUserCouponList(UserCoupon userCoupon);
    UserCoupon selectUserCouponById(Long userCouponId);
    int insertUserCoupon(UserCoupon userCoupon);
    int updateUserCoupon(UserCoupon userCoupon);
    
    int countUserReceive(@Param("userId") Long userId, @Param("couponId") Long couponId);
    
    // 更新状态（使用或过期）
    int updateStatus(@Param("userCouponId") Long userCouponId, @Param("status") String status, @Param("orderId") Long orderId);
}