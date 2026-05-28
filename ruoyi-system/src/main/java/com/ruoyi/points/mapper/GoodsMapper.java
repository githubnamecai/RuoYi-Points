package com.ruoyi.points.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.points.domain.Goods;

/**
 * 商品 Mapper
 */
public interface GoodsMapper
{
    List<Goods> selectGoodsList(Goods goods);

    /** H5 端商品列表（仅上架） */
    List<Goods> selectH5GoodsList(Goods goods);

    Goods selectGoodsById(Long goodsId);

    int insertGoods(Goods goods);

    int updateGoods(Goods goods);

    int deleteGoodsByIds(Long[] goodsIds);

    /**
     * 乐观锁扣减库存
     * UPDATE t_goods SET stock=stock-#{qty},sales=sales+#{qty},version=version+1
     * WHERE goods_id=#{id} AND stock>=#{qty} AND version=#{version}
     */
    int decreaseStock(@Param("goodsId") Long goodsId,
                      @Param("quantity") Integer quantity,
                      @Param("version") Integer version);

    /** 释放库存（关闭订单时回滚） */
    int increaseStock(@Param("goodsId") Long goodsId,
                      @Param("quantity") Integer quantity);

    /** 修改状态（上下架） */
    int updateStatus(@Param("goodsId") Long goodsId, @Param("status") String status);

    /** 统计某用户对某商品已成功兑换数量（用于限兑校验） */
    int countUserExchangeQuantity(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
}
