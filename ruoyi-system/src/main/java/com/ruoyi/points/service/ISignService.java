package com.ruoyi.points.service;

import com.ruoyi.points.domain.SignConfig;
import com.ruoyi.points.domain.SignRecord;
import com.ruoyi.points.domain.vo.SignInfoVO;

public interface ISignService
{
    SignConfig getConfig();
    int updateConfig(SignConfig config);

    /**
     * 获取签到信息（包含本月日历、连续天数、可补签日期）
     */
    SignInfoVO getSignInfo(Long userId);

    /**
     * 执行签到，返回签到记录
     */
    SignRecord doSign(Long userId);

    /**
     * 补签（指定日期 yyyy-MM-dd）
     */
    SignRecord repair(Long userId, String date);
}
