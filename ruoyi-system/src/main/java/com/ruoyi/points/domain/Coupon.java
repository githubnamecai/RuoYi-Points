package com.ruoyi.points.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 优惠券主表 t_coupon
 */
public class Coupon extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long couponId;

    @Excel(name = "部门ID")
    private Long deptId;

    private Long parentId;

    @Excel(name = "优惠券名称")
    private String couponName;

    /** 0满减券 1折扣券 2无门槛券 */
    @Excel(name = "优惠券类型", readConverterExp = "0=满减券,1=折扣券,2=无门槛券")
    private String couponType;

    @Excel(name = "使用门槛")
    private Integer minAmount;

    @Excel(name = "优惠面值")
    private Integer discountValue;

    @Excel(name = "发行总量")
    private Integer totalCount;

    @Excel(name = "已发放数量")
    private Integer issuedCount;

    @Excel(name = "每人限领数量")
    private Integer limitPerUser;

    /** 0全部商品 1指定分类 2指定商品 */
    @Excel(name = "适用范围", readConverterExp = "0=全部商品,1=指定分类,2=指定商品")
    private String useType;

    /** 0绝对时间 1相对天数 */
    @Excel(name = "有效期类型", readConverterExp = "0=绝对时间,1=相对天数")
    private String timeType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Integer validDays;

    /** 0正常 1停用 2过期 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用,2=过期")
    private String status;

    private String delFlag;

    // 非表字段：用于接收前端传来的商品/分类ID列表
    private Long[] refIds;

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }
    public String getCouponType() { return couponType; }
    public void setCouponType(String couponType) { this.couponType = couponType; }
    public Integer getMinAmount() { return minAmount; }
    public void setMinAmount(Integer minAmount) { this.minAmount = minAmount; }
    public Integer getDiscountValue() { return discountValue; }
    public void setDiscountValue(Integer discountValue) { this.discountValue = discountValue; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getIssuedCount() { return issuedCount; }
    public void setIssuedCount(Integer issuedCount) { this.issuedCount = issuedCount; }
    public Integer getLimitPerUser() { return limitPerUser; }
    public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }
    public String getUseType() { return useType; }
    public void setUseType(String useType) { this.useType = useType; }
    public String getTimeType() { return timeType; }
    public void setTimeType(String timeType) { this.timeType = timeType; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Integer getValidDays() { return validDays; }
    public void setValidDays(Integer validDays) { this.validDays = validDays; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public Long[] getRefIds() { return refIds; }
    public void setRefIds(Long[] refIds) { this.refIds = refIds; }
}