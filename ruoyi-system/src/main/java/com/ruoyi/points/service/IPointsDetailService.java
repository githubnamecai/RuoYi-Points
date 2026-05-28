package com.ruoyi.points.service;

import java.util.List;
import com.ruoyi.points.domain.PointsDetail;
import com.ruoyi.points.domain.vo.PointsStatVO;

public interface IPointsDetailService
{
    List<PointsDetail> selectDetailList(PointsDetail detail);
    List<PointsDetail> selectUserDetailList(Long userId);

    /**
     * 记录积分明细（统一入口，由 OrderService / SignService / 管理员调整 调用）
     */
    int record(Long userId, String changeType, String sourceType, String sourceId,
               Integer changePoints, Integer balance, String description, String operator);

    /** 平台统计 */
    PointsStatVO stat();
}
