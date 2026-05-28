package com.ruoyi.points.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 积分变动明细 t_points_detail
 *
 * @author ruoyi-points
 */
public class PointsDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "明细ID")
    private Long detailId;

    @Excel(name = "用户ID")
    private Long userId;

    /** 冗余昵称/手机（列表展示用） */
    @Excel(name = "用户昵称")
    private String nickname;

    @Excel(name = "手机号")
    private String phone;

    /** 0增加 1扣减 */
    @Excel(name = "类型", readConverterExp = "0=增加,1=扣减")
    private String changeType;

    @Excel(name = "来源")
    private String sourceType;

    private String sourceId;

    @Excel(name = "变动积分")
    private Integer changePoints;

    @Excel(name = "余额")
    private Integer balance;

    @Excel(name = "描述")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    public Integer getChangePoints() { return changePoints; }
    public void setChangePoints(Integer changePoints) { this.changePoints = changePoints; }
    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    @Override public Date getCreateTime() { return createTime; }
    @Override public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
