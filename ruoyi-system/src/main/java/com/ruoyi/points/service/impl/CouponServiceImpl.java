package com.ruoyi.points.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.points.domain.Coupon;
import com.ruoyi.points.domain.Goods;
import com.ruoyi.points.domain.UserCoupon;
import com.ruoyi.points.mapper.CouponMapper;
import com.ruoyi.points.mapper.GoodsMapper;
import com.ruoyi.points.mapper.UserCouponMapper;
import com.ruoyi.points.service.ICouponService;

@Service
public class CouponServiceImpl implements ICouponService
{
    @Autowired private CouponMapper couponMapper;
    @Autowired private UserCouponMapper userCouponMapper;
    @Autowired private GoodsMapper goodsMapper;

    @Override
    @DataScope(deptAlias = "c")
    public List<Coupon> selectCouponList(Coupon coupon)
    {
        return couponMapper.selectCouponList(coupon);
    }

    @Override
    public Coupon selectCouponById(Long couponId)
    {
        Coupon c = couponMapper.selectCouponById(couponId);
        if (c != null && ("1".equals(c.getUseType()) || "2".equals(c.getUseType()))) {
            List<Long> refIds = couponMapper.selectCouponGoodsIds(couponId);
            c.setRefIds(refIds.toArray(new Long[0]));
        }
        return c;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCoupon(Coupon coupon)
    {
        checkPermission(coupon);
        coupon.setDeptId(SecurityUtils.getDeptId());
        
        int rows = couponMapper.insertCoupon(coupon);
        if (rows > 0 && coupon.getRefIds() != null && coupon.getRefIds().length > 0) {
            couponMapper.insertCouponGoods(coupon.getCouponId(), coupon.getRefIds());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCoupon(Coupon coupon)
    {
        checkPermission(coupon);
        int rows = couponMapper.updateCoupon(coupon);
        if (rows > 0) {
            couponMapper.deleteCouponGoodsByCouponId(coupon.getCouponId());
            if (coupon.getRefIds() != null && coupon.getRefIds().length > 0) {
                couponMapper.insertCouponGoods(coupon.getCouponId(), coupon.getRefIds());
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCouponByIds(Long[] couponIds)
    {
        for (Long id : couponIds) {
            couponMapper.deleteCouponGoodsByCouponId(id);
        }
        return couponMapper.deleteCouponByIds(couponIds);
    }

    /**
     * 权限校验：若设置为"全部商品"，只有超级管理员可以操作
     */
    private void checkPermission(Coupon coupon) {
        if ("0".equals(coupon.getUseType()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            throw new ServiceException("仅超级管理员可创建全场通用的优惠券");
        }
    }

    @Override
    public List<Coupon> selectH5CouponList()
    {
        Coupon q = new Coupon();
        q.setStatus("0"); // 正常
        List<Coupon> list = couponMapper.selectCouponList(q);
        // 过滤掉已过期的或已发完的
        Date now = new Date();
        return list.stream().filter(c -> {
            if ("0".equals(c.getTimeType()) && c.getEndTime() != null && c.getEndTime().before(now)) {
                return false;
            }
            if (c.getTotalCount() != -1 && c.getIssuedCount() >= c.getTotalCount()) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int issueCoupon(Long couponId, Long[] userIds, String operator)
    {
        Coupon c = couponMapper.selectCouponById(couponId);
        if (c == null || !"0".equals(c.getStatus())) {
            throw new ServiceException("优惠券不存在或非正常状态");
        }
        
        int successCount = 0;
        for (Long uid : userIds) {
            if (c.getTotalCount() != -1 && c.getIssuedCount() + successCount >= c.getTotalCount()) {
                break; // 发完了
            }
            // 校验限领
            int received = userCouponMapper.countUserReceive(uid, couponId);
            if (received < c.getLimitPerUser()) {
                UserCoupon uc = buildUserCoupon(c, uid, "1");
                userCouponMapper.insertUserCoupon(uc);
                couponMapper.increaseIssuedCount(couponId);
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCoupon receiveCoupon(Long couponId, Long userId)
    {
        Coupon c = couponMapper.selectCouponById(couponId);
        if (c == null || !"0".equals(c.getStatus())) {
            throw new ServiceException("优惠券不存在或不可领取");
        }
        if (c.getTotalCount() != -1 && c.getIssuedCount() >= c.getTotalCount()) {
            throw new ServiceException("优惠券已被领完");
        }
        
        Date now = new Date();
        if ("0".equals(c.getTimeType()) && c.getEndTime() != null && c.getEndTime().before(now)) {
            throw new ServiceException("优惠券已过期");
        }
        
        int received = userCouponMapper.countUserReceive(userId, couponId);
        if (received >= c.getLimitPerUser()) {
            throw new ServiceException("已达到领取上限");
        }
        
        int rows = couponMapper.increaseIssuedCount(couponId);
        if (rows <= 0) {
            throw new ServiceException("领取失败，可能已被抢空");
        }
        
        UserCoupon uc = buildUserCoupon(c, userId, "0");
        userCouponMapper.insertUserCoupon(uc);
        return uc;
    }

    private UserCoupon buildUserCoupon(Coupon c, Long userId, String receiveType) {
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(c.getCouponId());
        uc.setReceiveType(receiveType);
        
        Date now = new Date();
        if ("0".equals(c.getTimeType())) {
            uc.setStartTime(c.getStartTime());
            uc.setEndTime(c.getEndTime());
        } else {
            uc.setStartTime(now);
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DATE, c.getValidDays());
            uc.setEndTime(cal.getTime());
        }
        return uc;
    }

    @Override
    public List<UserCoupon> selectUserCouponList(UserCoupon userCoupon)
    {
        return userCouponMapper.selectUserCouponList(userCoupon);
    }

    @Override
    public List<UserCoupon> selectAvailableCoupons(Long userId, Long goodsId, Integer totalPoints)
    {
        Goods goods = goodsMapper.selectGoodsById(goodsId);
        if (goods == null) return null;
        
        UserCoupon q = new UserCoupon();
        q.setUserId(userId);
        q.setStatus("0"); // 未使用
        List<UserCoupon> list = userCouponMapper.selectUserCouponList(q);
        
        Date now = new Date();
        return list.stream().filter(uc -> {
            // 校验过期
            if (uc.getEndTime().before(now)) return false;
            // 校验门槛
            if (uc.getCoupon().getMinAmount() > 0 && totalPoints < uc.getCoupon().getMinAmount()) return false;
            
            // 校验范围
            String useType = uc.getCoupon().getUseType();
            if ("0".equals(useType)) return true;
            
            List<Long> refIds = couponMapper.selectCouponGoodsIds(uc.getCouponId());
            if ("1".equals(useType)) {
                // 分类
                return refIds.contains(goods.getCategoryId());
            } else if ("2".equals(useType)) {
                // 指定商品
                return refIds.contains(goodsId);
            }
            return false;
        }).collect(Collectors.toList());
    }
}