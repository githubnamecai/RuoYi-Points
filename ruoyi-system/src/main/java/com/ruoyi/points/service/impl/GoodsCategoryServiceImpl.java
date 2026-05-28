package com.ruoyi.points.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.points.domain.GoodsCategory;
import com.ruoyi.points.mapper.GoodsCategoryMapper;
import com.ruoyi.points.service.IGoodsCategoryService;

@Service
public class GoodsCategoryServiceImpl implements IGoodsCategoryService
{
    @Autowired
    private GoodsCategoryMapper categoryMapper;

    @Override
    public List<GoodsCategory> selectCategoryList(GoodsCategory category)
    {
        return categoryMapper.selectCategoryList(category);
    }

    @Override
    public List<GoodsCategory> buildCategoryTree(List<GoodsCategory> list)
    {
        List<GoodsCategory> result = new ArrayList<>();
        List<Long> tempIds = list.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
        for (GoodsCategory c : list)
        {
            if (!tempIds.contains(c.getParentId()))
            {
                recursionFn(list, c);
                result.add(c);
            }
        }
        if (result.isEmpty()) result.addAll(list);
        return result;
    }

    @Override
    public List<TreeSelect> buildCategoryTreeSelect(List<GoodsCategory> list)
    {
        List<GoodsCategory> tree = buildCategoryTree(list);
        return tree.stream().map(c -> {
            TreeSelect ts = new TreeSelect();
            ts.setId(c.getCategoryId());
            ts.setLabel(c.getCategoryName());
            ts.setChildren(c.getChildren() == null ? null
                : c.getChildren().stream().map(child -> {
                    TreeSelect s = new TreeSelect();
                    s.setId(child.getCategoryId());
                    s.setLabel(child.getCategoryName());
                    return s;
                }).collect(Collectors.toList()));
            return ts;
        }).collect(Collectors.toList());
    }

    private void recursionFn(List<GoodsCategory> list, GoodsCategory parent)
    {
        List<GoodsCategory> children = list.stream()
            .filter(c -> Objects.equals(c.getParentId(), parent.getCategoryId()))
            .collect(Collectors.toList());
        parent.setChildren(children);
        for (GoodsCategory child : children) recursionFn(list, child);
    }

    @Override
    public GoodsCategory selectCategoryById(Long categoryId)
    {
        return categoryMapper.selectCategoryById(categoryId);
    }

    @Override
    public int insertCategory(GoodsCategory category)
    {
        // 拼接 ancestors
        if (category.getParentId() != null && category.getParentId() > 0)
        {
            GoodsCategory parent = categoryMapper.selectCategoryById(category.getParentId());
            if (parent == null) throw new ServiceException("父分类不存在");
            category.setAncestors(parent.getAncestors() + "," + parent.getCategoryId());
        }
        else
        {
            category.setParentId(0L);
            category.setAncestors("0");
        }
        if (StringUtils.isEmpty(category.getStatus())) category.setStatus("0");
        return categoryMapper.insertCategory(category);
    }

    @Override
    public int updateCategory(GoodsCategory category)
    {
        return categoryMapper.updateCategory(category);
    }

    @Override
    public int deleteCategoryById(Long categoryId)
    {
        if (categoryMapper.hasChildByCategoryId(categoryId) > 0)
            throw new ServiceException("存在子分类，不允许删除");
        if (categoryMapper.checkCategoryUseInGoods(categoryId) > 0)
            throw new ServiceException("分类下存在商品，不允许删除");
        return categoryMapper.deleteCategoryById(categoryId);
    }
}
