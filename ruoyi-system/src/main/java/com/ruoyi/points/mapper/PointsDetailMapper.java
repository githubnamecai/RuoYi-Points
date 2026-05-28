package com.ruoyi.points.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.points.domain.PointsDetail;

public interface PointsDetailMapper
{
    /** 管理端 查询全平台积分明细（支持手机号/类型/来源/时间） */
    List<PointsDetail> selectDetailList(PointsDetail detail);

    /** H5 用户查询自身明细 */
    List<PointsDetail> selectUserDetailList(@Param("userId") Long userId);

    int insert(PointsDetail detail);

    /** 平台累计发放/消耗 */
    Long sumByChangeType(@Param("changeType") String changeType);

    /** 今日发放/消耗 */
    Long sumTodayByChangeType(@Param("changeType") String changeType);
}
