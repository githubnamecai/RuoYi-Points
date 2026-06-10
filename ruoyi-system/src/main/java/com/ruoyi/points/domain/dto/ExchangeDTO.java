package com.ruoyi.points.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 兑换请求 DTO
 */
public class ExchangeDTO
{
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;

    @Min(value = 1, message = "数量至少为1")
    private Integer quantity = 1;

    /** 实物订单需提供地址ID（虚拟商品可不填） */
    private Long addressId;

    private String remark;

    /** 优惠券记录ID（可选） */
    private Long userCouponId;

    /** 实物商品支付金额（前端计算传入） */
    private BigDecimal payAmount;

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Long getUserCouponId() { return userCouponId; }
    public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
    public BigDecimal getPayAmount() { return payAmount; }
    public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }
}
