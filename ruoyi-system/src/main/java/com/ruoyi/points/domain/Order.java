package com.ruoyi.points.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 兑换订单对象 t_order
 *
 * @author ruoyi-points
 */
public class Order extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "订单ID")
    private Long orderId;

    @Excel(name = "订单编号")
    private String orderNo;

    private Long userId;

    @Excel(name = "用户手机")
    private String userPhone;

    private Long goodsId;

    @Excel(name = "商品名称")
    private String goodsName;

    private String goodsCover;

    /** 0实物 1虚拟 */
    @Excel(name = "商品类型", readConverterExp = "0=实物,1=虚拟")
    private String goodsType;

    @Excel(name = "数量")
    private Integer quantity;

    private Long deptId;

    private Long parentId;

    @Excel(name = "消耗积分")
    private Integer pointsUsed;

    /** 0待发货 1已发货 2已完成 3已关闭 */
    @Excel(name = "状态", readConverterExp = "0=待发货,1=已发货,2=已完成,3=已关闭")
    private String status;

    @Excel(name = "收件人")
    private String consignee;

    private String phone;

    @Excel(name = "收货地址")
    private String address;

    @Excel(name = "物流公司")
    private String expressCompany;

    @Excel(name = "物流单号")
    private String expressNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shipTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    private String closeReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date closeTime;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public String getGoodsCover() { return goodsCover; }
    public void setGoodsCover(String goodsCover) { this.goodsCover = goodsCover; }
    public String getGoodsType() { return goodsType; }
    public void setGoodsType(String goodsType) { this.goodsType = goodsType; }
    public Integer getQuantity() { return quantity; }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getPointsUsed() { return pointsUsed; }
    public void setPointsUsed(Integer pointsUsed) { this.pointsUsed = pointsUsed; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getConsignee() { return consignee; }
    public void setConsignee(String consignee) { this.consignee = consignee; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getExpressCompany() { return expressCompany; }
    public void setExpressCompany(String expressCompany) { this.expressCompany = expressCompany; }
    public String getExpressNo() { return expressNo; }
    public void setExpressNo(String expressNo) { this.expressNo = expressNo; }
    public Date getShipTime() { return shipTime; }
    public void setShipTime(Date shipTime) { this.shipTime = shipTime; }
    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
    public String getCloseReason() { return closeReason; }
    public void setCloseReason(String closeReason) { this.closeReason = closeReason; }
    public Date getCloseTime() { return closeTime; }
    public void setCloseTime(Date closeTime) { this.closeTime = closeTime; }
}
