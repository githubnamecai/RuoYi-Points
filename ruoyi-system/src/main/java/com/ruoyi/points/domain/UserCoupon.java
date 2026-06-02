package com.ruoyi.points.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 用户领券记录 t_user_coupon
 */
public class UserCoupon
{
    private Long userCouponId;

    @Excel(name = "H5用户ID")
    private Long userId;

    private Long couponId;

    /** 0主动领取 1后台发放 */
    @Excel(name = "获取方式", readConverterExp = "0=主动领取,1=后台发放")
    private String receiveType;

    /** 0未使用 1已使用 2已过期 */
    @Excel(name = "使用状态", readConverterExp = "0=未使用,1=已使用,2=已过期")
    private String status;

    private Long orderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "生效时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "过期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    // 冗余查询字段
    private Coupon coupon;
    private String nickname;
    private String phone;

    public Long getUserCouponId() { return userCouponId; }
    public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public String getReceiveType() { return receiveType; }
    public void setReceiveType(String receiveType) { this.receiveType = receiveType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Date getUseTime() { return useTime; }
    public void setUseTime(Date useTime) { this.useTime = useTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public Coupon getCoupon() { return coupon; }
    public void setCoupon(Coupon coupon) { this.coupon = coupon; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}