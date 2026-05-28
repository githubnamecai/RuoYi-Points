package com.ruoyi.web.controller.points;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.points.domain.PointsDetail;
import com.ruoyi.points.service.IPointsDetailService;

@RestController
@RequestMapping("/points/detail")
public class PointsDetailController extends BaseController
{
    @Autowired private IPointsDetailService detailService;

    @PreAuthorize("@ss.hasPermi('points:detail:list')")
    @GetMapping("/list")
    public TableDataInfo list(PointsDetail detail)
    {
        startPage();
        return getDataTable(detailService.selectDetailList(detail));
    }

    @GetMapping("/stat")
    public AjaxResult stat() { return success(detailService.stat()); }

    @PreAuthorize("@ss.hasPermi('points:detail:export')")
    @Log(title = "积分明细导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PointsDetail detail)
    {
        List<PointsDetail> list = detailService.selectDetailList(detail);
        ExcelUtil<PointsDetail> util = new ExcelUtil<>(PointsDetail.class);
        util.exportExcel(response, list, "积分明细");
    }
}
