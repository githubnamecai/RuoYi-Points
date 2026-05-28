package com.ruoyi.points.mapper;

import java.util.List;
import com.ruoyi.points.domain.PointsRule;

public interface PointsRuleMapper
{
    List<PointsRule> selectRuleList(PointsRule rule);
    PointsRule selectRuleById(Long ruleId);
    PointsRule selectRuleByCode(String ruleCode);
    int insertRule(PointsRule rule);
    int updateRule(PointsRule rule);
    int deleteRuleByIds(Long[] ruleIds);
}
