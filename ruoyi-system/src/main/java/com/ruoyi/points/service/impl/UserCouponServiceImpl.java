package com.ruoyi.points.service.impl;

import com.ruoyi.points.domain.UserAddress;
import com.ruoyi.points.domain.UserCoupon;
import com.ruoyi.points.mapper.UserAddressMapper;
import com.ruoyi.points.mapper.UserCouponMapper;
import com.ruoyi.points.service.IUserAddressService;
import com.ruoyi.points.service.IUserCouponService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserCouponServiceImpl implements IUserCouponService
{
    @Autowired
    private UserCouponMapper couponMapper;

    @Override public List<UserCoupon> selectUserCouponList(UserCoupon userCoupon) {
        return couponMapper.selectUserCouponList(userCoupon); }

    @Override public UserCoupon selectUserCouponById(Long userCouponId) { return couponMapper.selectUserCouponById(userCouponId); }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUserCoupon(UserCoupon userCoupon)
    {
        return couponMapper.insertUserCoupon(userCoupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUserCoupon(UserCoupon userCoupon)
    {

        return couponMapper.updateUserCoupon(userCoupon);
    }


    @Override
    public int countUserReceive(Long userId,Long couponId)
    { return couponMapper.countUserReceive(userId,couponId); }


    // 更新状态（使用或过期）
    @Override
    public int updateStatus(Long userCouponId,String status,Long orderId)
    { return couponMapper.updateStatus(userCouponId,status,orderId); }



}
