package com.ruoyi.qrcode.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 二维码点位信息对象 biz_qrcode_store
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class BizQrcodeStore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String phone;

    /** 用途 */
    @Excel(name = "用途")
    private String purpose;

    /** 二维码访问链接 */
    @Excel(name = "二维码访问链接")
    private String qrcodeUrl;

    /** 二码照片 */
    @Excel(name = "二码照片")
    private String qrPhoto;

    /** 对接人 */
    @Excel(name = "对接人")
    private String contactPerson;

    /** 扫码次数 */
    @Excel(name = "扫码次数")
    private Long scanCount;

    /** 排序号，数字越小越靠前 */
    @Excel(name = "排序号，数字越小越靠前")
    private Long sortNum;

    /** 扩展字段1 */
    @Excel(name = "扩展字段1")
    private String extInfo;



    /** 删除标记 0正常 1删除 */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }

    public void setPurpose(String purpose) 
    {
        this.purpose = purpose;
    }

    public String getPurpose() 
    {
        return purpose;
    }

    public void setQrcodeUrl(String qrcodeUrl) 
    {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getQrcodeUrl() 
    {
        return qrcodeUrl;
    }

    public void setQrPhoto(String qrPhoto) 
    {
        this.qrPhoto = qrPhoto;
    }

    public String getQrPhoto() 
    {
        return qrPhoto;
    }

    public void setContactPerson(String contactPerson) 
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson() 
    {
        return contactPerson;
    }

    public void setScanCount(Long scanCount) 
    {
        this.scanCount = scanCount;
    }

    public Long getScanCount() 
    {
        return scanCount;
    }

    public void setSortNum(Long sortNum) 
    {
        this.sortNum = sortNum;
    }

    public Long getSortNum() 
    {
        return sortNum;
    }

    public void setExtInfo(String extInfo) 
    {
        this.extInfo = extInfo;
    }

    public String getExtInfo() 
    {
        return extInfo;
    }



    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("address", getAddress())
            .append("phone", getPhone())
            .append("purpose", getPurpose())
            .append("qrcodeUrl", getQrcodeUrl())
            .append("qrPhoto", getQrPhoto())
            .append("contactPerson", getContactPerson())
            .append("scanCount", getScanCount())
            .append("sortNum", getSortNum())
            .append("extInfo", getExtInfo())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
