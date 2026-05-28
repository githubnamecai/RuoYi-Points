package com.ruoyi.points.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 签到记录 t_sign_record
 *
 * @author ruoyi-points
 */
public class SignRecord
{
    private Long recordId;
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date signDate;

    private Integer points;
    private Integer continuousDays;
    /** 0否 1是 */
    private String isRepair;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Date getSignDate() { return signDate; }
    public void setSignDate(Date signDate) { this.signDate = signDate; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public Integer getContinuousDays() { return continuousDays; }
    public void setContinuousDays(Integer continuousDays) { this.continuousDays = continuousDays; }
    public String getIsRepair() { return isRepair; }
    public void setIsRepair(String isRepair) { this.isRepair = isRepair; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
