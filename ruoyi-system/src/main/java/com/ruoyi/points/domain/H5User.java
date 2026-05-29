package com.ruoyi.points.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * H5 用户对象 t_user（独立于 sys_user）
 *
 * @author ruoyi-points
 */
public class H5User extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "用户ID")
    private Long userId;

    @Excel(name = "昵称")
    private String nickname;

    @Excel(name = "手机号")
    private String phone;

    private String password;

    private String avatar;

    @Excel(name = "积分余额")
    private Integer pointsBalance;

    @Excel(name = "累计获得")
    private Integer totalEarned;

    @Excel(name = "累计消耗")
    private Integer totalConsumed;

    private Integer continuousDays;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastSignDate;

    /** 0正常 1冻结 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=冻结")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "注册时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    private String lastLoginIp;
    private String delFlag;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getPointsBalance() { return pointsBalance; }
    public void setPointsBalance(Integer pointsBalance) { this.pointsBalance = pointsBalance; }
    public Integer getTotalEarned() { return totalEarned; }
    public void setTotalEarned(Integer totalEarned) { this.totalEarned = totalEarned; }
    public Integer getTotalConsumed() { return totalConsumed; }
    public void setTotalConsumed(Integer totalConsumed) { this.totalConsumed = totalConsumed; }
    public Integer getContinuousDays() { return continuousDays; }
    public void setContinuousDays(Integer continuousDays) { this.continuousDays = continuousDays; }
    public Date getLastSignDate() { return lastSignDate; }
    public void setLastSignDate(Date lastSignDate) { this.lastSignDate = lastSignDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getRegisterTime() { return registerTime; }
    public void setRegisterTime(Date registerTime) { this.registerTime = registerTime; }
    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    public String getLastLoginIp() { return lastLoginIp; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
