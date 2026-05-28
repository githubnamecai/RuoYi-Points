package com.ruoyi.points.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 管理员手动调整积分 DTO
 */
public class PointsAdjustDTO
{
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 0增加 1扣减 */
    @NotNull(message = "调整类型不能为空")
    private String changeType;

    @NotNull(message = "积分不能为空")
    @Min(value = 1, message = "积分必须大于0")
    private Integer points;

    private String reason;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
