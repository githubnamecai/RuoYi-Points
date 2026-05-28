package com.ruoyi.points.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.points.domain.PointsRule;
import com.ruoyi.points.mapper.PointsRuleMapper;
import com.ruoyi.points.service.IPointsRuleService;

@Service
public class PointsRuleServiceImpl implements IPointsRuleService
{
    @Autowired
    private PointsRuleMapper ruleMapper;

    @Override public List<PointsRule> selectRuleList(PointsRule rule) { return ruleMapper.selectRuleList(rule); }
    @Override public PointsRule selectRuleById(Long ruleId) { return ruleMapper.selectRuleById(ruleId); }
    @Override public PointsRule selectRuleByCode(String ruleCode) { return ruleMapper.selectRuleByCode(ruleCode); }
    @Override public int insertRule(PointsRule rule) { return ruleMapper.insertRule(rule); }
    @Override public int updateRule(PointsRule rule) { return ruleMapper.updateRule(rule); }
    @Override public int deleteRuleByIds(Long[] ruleIds) { return ruleMapper.deleteRuleByIds(ruleIds); }
}
