package com.ruoyi.points.service;

import java.util.List;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.points.domain.GoodsCategory;

public interface IGoodsCategoryService
{
    List<GoodsCategory> selectCategoryList(GoodsCategory category);
    List<GoodsCategory> buildCategoryTree(List<GoodsCategory> list);
    List<TreeSelect> buildCategoryTreeSelect(List<GoodsCategory> list);
    GoodsCategory selectCategoryById(Long categoryId);
    int insertCategory(GoodsCategory category);
    int updateCategory(GoodsCategory category);
    int deleteCategoryById(Long categoryId);
}
