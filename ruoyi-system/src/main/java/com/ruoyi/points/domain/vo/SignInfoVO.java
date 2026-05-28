package com.ruoyi.points.domain.vo;

import java.util.List;

/**
 * H5 签到信息 VO
 *
 * @author ruoyi-points
 */
public class SignInfoVO
{
    /** 今日是否已签到 */
    private boolean signedToday;
    /** 连续签到天数 */
    private Integer continuousDays;
    /** 累计签到天数（本月） */
    private Integer monthlyDays;
    /** 当月已签到的日期(yyyy-MM-dd) */
    private List<String> signedDates;
    /** 可补签的日期 */
    private List<String> repairableDates;
    /** 签到配置 */
    private SignConfigVO config;

    public static class SignConfigVO
    {
        private Integer basePoints;
        private String continuousReward;
        private String repairEnabled;
        private Integer repairCost;
        private Integer repairMaxDays;

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
    }

    public boolean isSignedToday() { return signedToday; }
    public void setSignedToday(boolean signedToday) { this.signedToday = signedToday; }
    public Integer getContinuousDays() { return continuousDays; }
    public void setContinuousDays(Integer continuousDays) { this.continuousDays = continuousDays; }
    public Integer getMonthlyDays() { return monthlyDays; }
    public void setMonthlyDays(Integer monthlyDays) { this.monthlyDays = monthlyDays; }
    public List<String> getSignedDates() { return signedDates; }
    public void setSignedDates(List<String> signedDates) { this.signedDates = signedDates; }
    public List<String> getRepairableDates() { return repairableDates; }
    public void setRepairableDates(List<String> repairableDates) { this.repairableDates = repairableDates; }
    public SignConfigVO getConfig() { return config; }
    public void setConfig(SignConfigVO config) { this.config = config; }
}
