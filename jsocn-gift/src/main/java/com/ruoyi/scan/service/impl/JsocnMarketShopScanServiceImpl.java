package com.ruoyi.scan.service.impl;

import java.util.List;

import com.ruoyi.qrcode.domain.BizQrcodeStore;
import com.ruoyi.qrcode.mapper.BizQrcodeStoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.scan.mapper.JsocnMarketShopScanMapper;
import com.ruoyi.scan.domain.JsocnMarketShopScan;
import com.ruoyi.scan.service.IJsocnMarketShopScanService;

/**
 * 扫码统计Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class JsocnMarketShopScanServiceImpl implements IJsocnMarketShopScanService 
{
    @Autowired
    private JsocnMarketShopScanMapper jsocnMarketShopScanMapper;
    @Autowired
    private BizQrcodeStoreMapper bizQrcodeStoreMapper;

    /**
     * 查询扫码统计
     * 
     * @param id 扫码统计主键
     * @return 扫码统计
     */
    @Override
    public JsocnMarketShopScan selectJsocnMarketShopScanById(Long id)
    {
        return jsocnMarketShopScanMapper.selectJsocnMarketShopScanById(id);
    }

    /**
     * 查询扫码统计列表
     * 
     * @param jsocnMarketShopScan 扫码统计
     * @return 扫码统计
     */
    @Override
    public List<JsocnMarketShopScan> selectJsocnMarketShopScanList(JsocnMarketShopScan jsocnMarketShopScan)
    {
        return jsocnMarketShopScanMapper.selectJsocnMarketShopScanList(jsocnMarketShopScan);
    }

    /**
     * 新增扫码统计
     * 
     * @param jsocnMarketShopScan 扫码统计
     * @return 结果
     */
    @Override
    public int insertJsocnMarketShopScan(JsocnMarketShopScan jsocnMarketShopScan)
    {
        BizQrcodeStore bizQrcodeStore = bizQrcodeStoreMapper.selectBizQrcodeStoreById(jsocnMarketShopScan.getQrcodeId());
        bizQrcodeStore.setScanCount(bizQrcodeStore.getScanCount() + 1);
        bizQrcodeStoreMapper.updateBizQrcodeStore(bizQrcodeStore);
        return jsocnMarketShopScanMapper.insertJsocnMarketShopScan(jsocnMarketShopScan);
    }

    /**
     * 修改扫码统计
     * 
     * @param jsocnMarketShopScan 扫码统计
     * @return 结果
     */
    @Override
    public int updateJsocnMarketShopScan(JsocnMarketShopScan jsocnMarketShopScan)
    {
        return jsocnMarketShopScanMapper.updateJsocnMarketShopScan(jsocnMarketShopScan);
    }

    /**
     * 批量删除扫码统计
     * 
     * @param ids 需要删除的扫码统计主键
     * @return 结果
     */
    @Override
    public int deleteJsocnMarketShopScanByIds(Long[] ids)
    {
        return jsocnMarketShopScanMapper.deleteJsocnMarketShopScanByIds(ids);
    }

    /**
     * 删除扫码统计信息
     * 
     * @param id 扫码统计主键
     * @return 结果
     */
    @Override
    public int deleteJsocnMarketShopScanById(Long id)
    {
        return jsocnMarketShopScanMapper.deleteJsocnMarketShopScanById(id);
    }
}
