package com.ruoyi.points.service;

import java.util.List;
import com.ruoyi.points.domain.Goods;

public interface IGoodsService
{
    List<Goods> selectGoodsList(Goods goods);
    List<Goods> selectH5GoodsList(Goods goods);
    Goods selectGoodsById(Long goodsId);
    int insertGoods(Goods goods);
    int updateGoods(Goods goods);
    int deleteGoodsByIds(Long[] goodsIds);
    int changeStatus(Long goodsId, String status);
}
