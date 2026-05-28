package com.ruoyi.points.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;

/**
 * 积分规则 t_points_rule
 *
 * @author ruoyi-points
 */
public class PointsRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long ruleId;

    @NotBlank(message = "规则编码不能为空")
    @Excel(name = "编码")
    private String ruleCode;

    @NotBlank(message = "规则名称不能为空")
    @Excel(name = "名称")
    private String ruleName;

    /** 0签到 1任务 2其他 */
    private String ruleType;

    @Excel(name = "奖励积分")
    private Integer rewardPoints;

    private Integer dailyLimit;

    private String configJson;

    private String status;

    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }
    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }
    public Integer getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; }
    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
