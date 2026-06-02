package com.ruoyi.web.controller.points;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.points.domain.Goods;
import com.ruoyi.points.service.IGoodsService;

/**
 * 商品管理
 */
@RestController
@RequestMapping("/points/goods")
public class GoodsController extends BaseController
{
    @Autowired
    private IGoodsService goodsService;

    @PreAuthorize("@ss.hasPermi('points:goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(Goods goods)
    {
        startPage();
        // 判断前端有没有传递当前用户的deptid，没有则后端获取当前用户的deptid
        if (goods.getDeptId() == null) goods.setDeptId(getDeptId());
        List<Goods> list = goodsService.selectGoodsList(goods);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('points:goods:query')")
    @GetMapping("/{goodsId}")
    public AjaxResult getInfo(@PathVariable Long goodsId)
    {
        return success(goodsService.selectGoodsById(goodsId));
    }

    @PreAuthorize("@ss.hasPermi('points:goods:add')")
    @Log(title = "商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Goods goods)
    {
        goods.setDeptId(getDeptId());
        goods.setCreateBy(getUsername());
        return toAjax(goodsService.insertGoods(goods));
    }

    @PreAuthorize("@ss.hasPermi('points:goods:edit')")
    @Log(title = "商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Goods goods)
    {
        if (goods.getDeptId() == null){
            goods.setDeptId(getDeptId());
        }
        goods.setUpdateBy(getUsername());
        return toAjax(goodsService.updateGoods(goods));
    }

    @PreAuthorize("@ss.hasPermi('points:goods:edit')")
    @Log(title = "商品上下架", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{goodsId}/{status}")
    public AjaxResult changeStatus(@PathVariable Long goodsId, @PathVariable String status)
    {
        return toAjax(goodsService.changeStatus(goodsId, status));
    }

    @PreAuthorize("@ss.hasPermi('points:goods:remove')")
    @Log(title = "商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{goodsIds}")
    public AjaxResult remove(@PathVariable Long[] goodsIds)
    {
        return toAjax(goodsService.deleteGoodsByIds(goodsIds));
    }
}
