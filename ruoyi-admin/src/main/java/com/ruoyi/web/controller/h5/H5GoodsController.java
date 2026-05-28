package com.ruoyi.web.controller.h5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.points.domain.Goods;
import com.ruoyi.points.domain.GoodsCategory;
import com.ruoyi.points.service.IGoodsCategoryService;
import com.ruoyi.points.service.IGoodsService;

@RestController
@RequestMapping("/h5-api/goods")
public class H5GoodsController extends H5BaseController
{
    @Autowired private IGoodsService goodsService;
    @Autowired private IGoodsCategoryService categoryService;

    /** 商品列表（瀑布流，按分页） */
    @GetMapping("/list")
    public TableDataInfo list(Goods goods)
    {
        startPage();
        return getDataTable(goodsService.selectH5GoodsList(goods));
    }

    /** 商品详情 */
    @GetMapping("/{goodsId}")
    public AjaxResult detail(@PathVariable Long goodsId)
    {
        return success(goodsService.selectGoodsById(goodsId));
    }

    /** 分类树（仅启用） */
    @GetMapping("/categories")
    public AjaxResult categories()
    {
        GoodsCategory q = new GoodsCategory();
        q.setStatus("0");
        return success(categoryService.buildCategoryTree(categoryService.selectCategoryList(q)));
    }
}
