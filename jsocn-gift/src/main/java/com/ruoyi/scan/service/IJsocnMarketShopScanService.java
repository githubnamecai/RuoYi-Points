package com.ruoyi.scan.service;

import java.util.List;
import com.ruoyi.scan.domain.JsocnMarketShopScan;

/**
 * 扫码统计Service接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IJsocnMarketShopScanService 
{
    /**
     * 查询扫码统计
     * 
     * @param id 扫码统计主键
     * @return 扫码统计
     */
    public JsocnMarketShopScan selectJsocnMarketShopScanById(Long id);

    /**
     * 查询扫码统计列表
     * 
     * @param jsocnMarketShopScan 扫码统计
     * @return 扫码统计集合
     */
    public List<JsocnMarketShopScan> selectJsocnMarketShopScanList(JsocnMarketShopScan jsocnMarketShopScan);

    /**
     * 新增扫码统计
     * 
     * @param jsocnMarketShopScan 扫码统计
     * @return 结果
     */
    public int insertJsocnMarketShopScan(JsocnMarketShopScan jsocnMarketShopScan);

    /**
     * 修改扫码统计
     * 
     * @param jsocnMarketShopScan 扫码统计
     * @return 结果
     */
    public int updateJsocnMarketShopScan(JsocnMarketShopScan jsocnMarketShopScan);

    /**
     * 批量删除扫码统计
     * 
     * @param ids 需要删除的扫码统计主键集合
     * @return 结果
     */
    public int deleteJsocnMarketShopScanByIds(Long[] ids);

    /**
     * 删除扫码统计信息
     * 
     * @param id 扫码统计主键
     * @return 结果
     */
    public int deleteJsocnMarketShopScanById(Long id);
}
