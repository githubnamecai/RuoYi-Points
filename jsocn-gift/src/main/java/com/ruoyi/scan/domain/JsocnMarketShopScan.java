package com.ruoyi.scan.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 扫码统计对象 jsocn_market_shop_scan
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public class JsocnMarketShopScan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long id;

    /** 二维码的ID */
    @Excel(name = "二维码的ID")
    private Long qrcodeId;

    /** 用户ID */
    private Long userId;

    /** 用户姓名 */
//    @Excel(name = "用户姓名")
    private String userName;

    /** 用户昵称 */
//    @Excel(name = "用户昵称")
    private String nickName;

    /** 手机号 */
//    @Excel(name = "手机号")
    private String phone;

    /** 访问方式 0-链接跳转 1-扫码访问 */
//    @Excel(name = "访问方式 0-链接跳转 1-扫码访问")
    private Integer visitType;

    /** 扫码时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "扫码时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** IP地址 */
    @Excel(name = "IP地址")
    private String ip;

    /** 设备型号 */
    @Excel(name = "设备型号")
    private String deviceModel;

    /** 系统版本 */
    @Excel(name = "系统版本")
    private String osVersion;

    /** 浏览器名称 */
    @Excel(name = "浏览器名称")
    private String browserName;

    /** navigator获取标识 */
    private String userAgent;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setQrcodeId(Long qrcodeId) 
    {
        this.qrcodeId = qrcodeId;
    }

    public Long getQrcodeId() 
    {
        return qrcodeId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public void setNickName(String nickName) 
    {
        this.nickName = nickName;
    }

    public String getNickName() 
    {
        return nickName;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }

    public void setVisitType(Integer visitType) 
    {
        this.visitType = visitType;
    }

    public Integer getVisitType() 
    {
        return visitType;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }

    public void setDeviceModel(String deviceModel) 
    {
        this.deviceModel = deviceModel;
    }

    public String getDeviceModel() 
    {
        return deviceModel;
    }

    public void setOsVersion(String osVersion) 
    {
        this.osVersion = osVersion;
    }

    public String getOsVersion() 
    {
        return osVersion;
    }

    public void setBrowserName(String browserName) 
    {
        this.browserName = browserName;
    }

    public String getBrowserName() 
    {
        return browserName;
    }

    public void setUserAgent(String userAgent) 
    {
        this.userAgent = userAgent;
    }

    public String getUserAgent() 
    {
        return userAgent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("qrcodeId", getQrcodeId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("nickName", getNickName())
            .append("phone", getPhone())
            .append("visitType", getVisitType())
            .append("startTime", getStartTime())
            .append("ip", getIp())
            .append("deviceModel", getDeviceModel())
            .append("osVersion", getOsVersion())
            .append("browserName", getBrowserName())
            .append("userAgent", getUserAgent())
            .append("remark", getRemark())
            .toString();
    }
}
