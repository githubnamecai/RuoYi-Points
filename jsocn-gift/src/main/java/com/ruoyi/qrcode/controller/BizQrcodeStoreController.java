package com.ruoyi.qrcode.controller;

import java.util.List;
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
import com.ruoyi.qrcode.domain.BizQrcodeStore;
import com.ruoyi.qrcode.service.IBizQrcodeStoreService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 二维码点位信息Controller
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@RestController
@RequestMapping("/qrcode/qrcodestore")
public class BizQrcodeStoreController extends BaseController
{
    @Autowired
    private IBizQrcodeStoreService bizQrcodeStoreService;

    /**
     * 查询二维码点位信息列表
     */
    @PreAuthorize("@ss.hasPermi('qrcode:qrcodestore:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizQrcodeStore bizQrcodeStore)
    {
        startPage();
        List<BizQrcodeStore> list = bizQrcodeStoreService.selectBizQrcodeStoreList(bizQrcodeStore);
        return getDataTable(list);
    }

    /**
     * 导出二维码点位信息列表
     */
    @PreAuthorize("@ss.hasPermi('qrcode:qrcodestore:export')")
    @Log(title = "二维码点位信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizQrcodeStore bizQrcodeStore)
    {
        List<BizQrcodeStore> list = bizQrcodeStoreService.selectBizQrcodeStoreList(bizQrcodeStore);
        ExcelUtil<BizQrcodeStore> util = new ExcelUtil<BizQrcodeStore>(BizQrcodeStore.class);
        util.exportExcel(response, list, "二维码点位信息数据");
    }

    /**
     * 获取二维码点位信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('qrcode:qrcodestore:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizQrcodeStoreService.selectBizQrcodeStoreById(id));
    }

    /**
     * 新增二维码点位信息
     */
    @PreAuthorize("@ss.hasPermi('qrcode:qrcodestore:add')")
    @Log(title = "二维码点位信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizQrcodeStore bizQrcodeStore)
    {
        bizQrcodeStore.setCreateBy(getUsername());
        return toAjax(bizQrcodeStoreService.insertBizQrcodeStore(bizQrcodeStore));
    }

    /**
     * 修改二维码点位信息
     */
    @PreAuthorize("@ss.hasPermi('qrcode:qrcodestore:edit')")
    @Log(title = "二维码点位信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizQrcodeStore bizQrcodeStore)
    {
        bizQrcodeStore.setUpdateBy(getUsername());
        return toAjax(bizQrcodeStoreService.updateBizQrcodeStore(bizQrcodeStore));
    }

    /**
     * 删除二维码点位信息
     */
    @PreAuthorize("@ss.hasPermi('qrcode:qrcodestore:remove')")
    @Log(title = "二维码点位信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizQrcodeStoreService.deleteBizQrcodeStoreByIds(ids));
    }
}
