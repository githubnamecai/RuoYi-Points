package com.ruoyi.points.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.points.domain.Goods;
import com.ruoyi.points.mapper.GoodsMapper;
import com.ruoyi.points.service.IGoodsService;

@Service
public class GoodsServiceImpl implements IGoodsService
{
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<Goods> selectGoodsList(Goods goods) { return goodsMapper.selectGoodsList(goods); }

    @Override
    public List<Goods> selectH5GoodsList(Goods goods) { return goodsMapper.selectH5GoodsList(goods); }

    @Override
    public Goods selectGoodsById(Long goodsId) { return goodsMapper.selectGoodsById(goodsId); }

    @Override
    public int insertGoods(Goods goods)
    {
        if (StringUtils.isEmpty(goods.getStatus())) goods.setStatus("0");
        if (StringUtils.isEmpty(goods.getGoodsType())) goods.setGoodsType("0");
        if (goods.getStock() == null) goods.setStock(0);
        if (goods.getSort() == null) goods.setSort(0);
        if (goods.getLimitPerUser() == null) goods.setLimitPerUser(0);
        return goodsMapper.insertGoods(goods);
    }

    @Override
    public int updateGoods(Goods goods) { return goodsMapper.updateGoods(goods); }

    @Override
    public int deleteGoodsByIds(Long[] goodsIds) { return goodsMapper.deleteGoodsByIds(goodsIds); }

    @Override
    public int changeStatus(Long goodsId, String status) { return goodsMapper.updateStatus(goodsId, status); }
}
