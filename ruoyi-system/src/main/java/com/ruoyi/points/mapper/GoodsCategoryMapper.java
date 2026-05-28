package com.ruoyi.points.mapper;

import java.util.List;
import com.ruoyi.points.domain.GoodsCategory;

/**
 * 商品分类 Mapper
 */
public interface GoodsCategoryMapper
{
    List<GoodsCategory> selectCategoryList(GoodsCategory category);

    GoodsCategory selectCategoryById(Long categoryId);

    int insertCategory(GoodsCategory category);

    int updateCategory(GoodsCategory category);

    int deleteCategoryById(Long categoryId);

    int hasChildByCategoryId(Long categoryId);

    int checkCategoryUseInGoods(Long categoryId);
}
