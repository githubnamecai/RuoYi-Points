package com.ruoyi.points.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户收货地址 t_user_address
 *
 * @author ruoyi-points
 */
public class UserAddress extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long addressId;
    private Long userId;

    @NotBlank(message = "收件人不能为空")
    private String consignee;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    private String province;
    private String city;
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    /** 0否 1是 */
    private String isDefault;

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getConsignee() { return consignee; }
    public void setConsignee(String consignee) { this.consignee = consignee; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getIsDefault() { return isDefault; }
    public void setIsDefault(String isDefault) { this.isDefault = isDefault; }
}
