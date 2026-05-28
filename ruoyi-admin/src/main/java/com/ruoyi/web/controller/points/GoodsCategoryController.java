package com.ruoyi.web.controller.points;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.points.domain.GoodsCategory;
import com.ruoyi.points.service.IGoodsCategoryService;

/**
 * 商品分类
 */
@RestController
@RequestMapping("/points/category")
public class GoodsCategoryController extends BaseController
{
    @Autowired
    private IGoodsCategoryService categoryService;

    @PreAuthorize("@ss.hasPermi('points:category:list')")
    @GetMapping("/list")
    public AjaxResult list(GoodsCategory category)
    {
        return success(categoryService.selectCategoryList(category));
    }

    @GetMapping("/tree")
    public AjaxResult tree(GoodsCategory category)
    {
        return success(categoryService.buildCategoryTree(categoryService.selectCategoryList(category)));
    }

    @GetMapping("/treeselect")
    public AjaxResult treeselect(GoodsCategory category)
    {
        return success(categoryService.buildCategoryTreeSelect(categoryService.selectCategoryList(category)));
    }

    @PreAuthorize("@ss.hasPermi('points:category:query')")
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId)
    {
        return success(categoryService.selectCategoryById(categoryId));
    }

    @PreAuthorize("@ss.hasPermi('points:category:add')")
    @Log(title = "商品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GoodsCategory category)
    {
        category.setCreateBy(getUsername());
        return toAjax(categoryService.insertCategory(category));
    }

    @PreAuthorize("@ss.hasPermi('points:category:edit')")
    @Log(title = "商品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GoodsCategory category)
    {
        category.setUpdateBy(getUsername());
        return toAjax(categoryService.updateCategory(category));
    }

    @PreAuthorize("@ss.hasPermi('points:category:remove')")
    @Log(title = "商品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryId}")
    public AjaxResult remove(@PathVariable Long categoryId)
    {
        return toAjax(categoryService.deleteCategoryById(categoryId));
    }
}
