package com.ruoyi.scan.controller;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.scan.domain.JsocnMarketShopScan;
import com.ruoyi.scan.service.IJsocnMarketShopScanService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 扫码统计Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/scan/scan")
public class JsocnMarketShopScanController extends BaseController
{
    @Autowired
    private IJsocnMarketShopScanService jsocnMarketShopScanService;

    /**
     * 查询扫码统计列表
     */
    @PreAuthorize("@ss.hasPermi('scan:scan:list')")
    @GetMapping("/list")
    public TableDataInfo list(JsocnMarketShopScan jsocnMarketShopScan)
    {
        startPage();
        List<JsocnMarketShopScan> list = jsocnMarketShopScanService.selectJsocnMarketShopScanList(jsocnMarketShopScan);
        return getDataTable(list);
    }

    /**
     * 导出扫码统计列表
     */
    @PreAuthorize("@ss.hasPermi('scan:scan:export')")
    @Log(title = "扫码统计", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JsocnMarketShopScan jsocnMarketShopScan)
    {
        List<JsocnMarketShopScan> list = jsocnMarketShopScanService.selectJsocnMarketShopScanList(jsocnMarketShopScan);
        ExcelUtil<JsocnMarketShopScan> util = new ExcelUtil<JsocnMarketShopScan>(JsocnMarketShopScan.class);
        util.exportExcel(response, list, "扫码统计数据");
    }

    /**
     * 获取扫码统计详细信息
     */
    @PreAuthorize("@ss.hasPermi('scan:scan:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(jsocnMarketShopScanService.selectJsocnMarketShopScanById(id));
    }

    /**
     * 新增扫码统计
     */
    @PreAuthorize("@ss.hasPermi('scan:scan:add')")
//    @Log(title = "扫码统计", businessType = BusinessType.INSERT)
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody JsocnMarketShopScan jsocnMarketShopScan, HttpServletRequest request)
    {
        if (StringUtils.isEmpty(jsocnMarketShopScan.getIp()))
        {
            jsocnMarketShopScan.setIp(IpUtils.getIpAddr(request));
        }
        if (jsocnMarketShopScan.getStartTime() == null)
        {
            jsocnMarketShopScan.setStartTime(new java.util.Date());
        }
        if (StringUtils.isEmpty(jsocnMarketShopScan.getUserAgent()))
        {
            jsocnMarketShopScan.setUserAgent(request.getHeader("User-Agent"));
        }
        return toAjax(jsocnMarketShopScanService.insertJsocnMarketShopScan(jsocnMarketShopScan));
    }

    /**
     * 修改扫码统计
     */
    @PreAuthorize("@ss.hasPermi('scan:scan:edit')")
    @Log(title = "扫码统计", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JsocnMarketShopScan jsocnMarketShopScan)
    {
        return toAjax(jsocnMarketShopScanService.updateJsocnMarketShopScan(jsocnMarketShopScan));
    }

    /**
     * 删除扫码统计
     */
    @PreAuthorize("@ss.hasPermi('scan:scan:remove')")
    @Log(title = "扫码统计", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(jsocnMarketShopScanService.deleteJsocnMarketShopScanByIds(ids));
    }
}
