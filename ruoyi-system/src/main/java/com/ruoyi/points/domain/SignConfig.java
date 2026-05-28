package com.ruoyi.points.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 签到配置 t_sign_config（单例，config_id=1）
 *
 * @author ruoyi-points
 */
public class SignConfig
{
    private Long configId;
    /** 0关 1开 */
    private String enabled;
    private Integer basePoints;
    /** 连续签到奖励 JSON 数组 [{day:7,points:50}] */
    private String continuousReward;
    /** 0否 1是 */
    private String repairEnabled;
    private Integer repairCost;
    private Integer repairMaxDays;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getConfigId() { return configId; }
    public void setConfigId(Long configId) { this.configId = configId; }
    public String getEnabled() { return enabled; }
    public void setEnabled(String enabled) { this.enabled = enabled; }
    public Integer getBasePoints() { return basePoints; }
    public void setBasePoints(Integer basePoints) { this.basePoints = basePoints; }
    public String getContinuousReward() { return continuousReward; }
    public void setContinuousReward(String continuousReward) { this.continuousReward = continuousReward; }
    public String getRepairEnabled() { return repairEnabled; }
    public void setRepairEnabled(String repairEnabled) { this.repairEnabled = repairEnabled; }
    public Integer getRepairCost() { return repairCost; }
    public void setRepairCost(Integer repairCost) { this.repairCost = repairCost; }
    public Integer getRepairMaxDays() { return repairMaxDays; }
    public void setRepairMaxDays(Integer repairMaxDays) { this.repairMaxDays = repairMaxDays; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
