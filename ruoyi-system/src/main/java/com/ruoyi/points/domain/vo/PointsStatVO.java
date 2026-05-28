package com.ruoyi.points.domain.vo;

/**
 * 积分统计 VO（管理端统计卡片）
 */
public class PointsStatVO
{
    /** 平台累计发放积分 */
    private Long totalIssued;
    /** 平台累计消耗积分 */
    private Long totalConsumed;
    /** 用户当前持有积分总和 */
    private Long totalBalance;
    /** 用户总数 */
    private Long userCount;
    /** 今日新增积分 */
    private Long todayIssued;
    /** 今日消耗积分 */
    private Long todayConsumed;

    public Long getTotalIssued() { return totalIssued; }
    public void setTotalIssued(Long totalIssued) { this.totalIssued = totalIssued; }
    public Long getTotalConsumed() { return totalConsumed; }
    public void setTotalConsumed(Long totalConsumed) { this.totalConsumed = totalConsumed; }
    public Long getTotalBalance() { return totalBalance; }
    public void setTotalBalance(Long totalBalance) { this.totalBalance = totalBalance; }
    public Long getUserCount() { return userCount; }
    public void setUserCount(Long userCount) { this.userCount = userCount; }
    public Long getTodayIssued() { return todayIssued; }
    public void setTodayIssued(Long todayIssued) { this.todayIssued = todayIssued; }
    public Long getTodayConsumed() { return todayConsumed; }
    public void setTodayConsumed(Long todayConsumed) { this.todayConsumed = todayConsumed; }
}
