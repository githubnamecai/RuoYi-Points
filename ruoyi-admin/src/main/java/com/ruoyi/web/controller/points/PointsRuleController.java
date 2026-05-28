package com.ruoyi.web.controller.points;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.points.domain.PointsRule;
import com.ruoyi.points.domain.SignConfig;
import com.ruoyi.points.service.IPointsRuleService;
import com.ruoyi.points.service.ISignService;

@RestController
@RequestMapping("/points/rule")
public class PointsRuleController extends BaseController
{
    @Autowired private IPointsRuleService ruleService;
    @Autowired private ISignService signService;

    @PreAuthorize("@ss.hasPermi('points:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(PointsRule rule)
    {
        startPage();
        return getDataTable(ruleService.selectRuleList(rule));
    }

    @PreAuthorize("@ss.hasPermi('points:rule:query')")
    @GetMapping("/{ruleId}")
    public AjaxResult getInfo(@PathVariable Long ruleId)
    {
        return success(ruleService.selectRuleById(ruleId));
    }

    @PreAuthorize("@ss.hasPermi('points:rule:edit')")
    @Log(title = "积分规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PointsRule rule)
    {
        rule.setCreateBy(getUsername());
        return toAjax(ruleService.insertRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('points:rule:edit')")
    @Log(title = "积分规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PointsRule rule)
    {
        rule.setUpdateBy(getUsername());
        return toAjax(ruleService.updateRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('points:rule:edit')")
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds)
    {
        return toAjax(ruleService.deleteRuleByIds(ruleIds));
    }

    // ============ 签到配置（单例） ============
    @GetMapping("/sign/config")
    public AjaxResult getSignConfig()
    {
        return success(signService.getConfig());
    }

    @PreAuthorize("@ss.hasPermi('points:sign:edit')")
    @Log(title = "签到配置", businessType = BusinessType.UPDATE)
    @PutMapping("/sign/config")
    public AjaxResult updateSignConfig(@RequestBody SignConfig config)
    {
        config.setUpdateBy(getUsername());
        return toAjax(signService.updateConfig(config));
    }
}
