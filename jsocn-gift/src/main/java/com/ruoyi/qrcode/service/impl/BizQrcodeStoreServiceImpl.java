package com.ruoyi.qrcode.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.qrcode.mapper.BizQrcodeStoreMapper;
import com.ruoyi.qrcode.domain.BizQrcodeStore;
import com.ruoyi.qrcode.service.IBizQrcodeStoreService;

/**
 * 二维码点位信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
@Service
public class BizQrcodeStoreServiceImpl implements IBizQrcodeStoreService 
{
    @Autowired
    private BizQrcodeStoreMapper bizQrcodeStoreMapper;

    /**
     * 查询二维码点位信息
     * 
     * @param id 二维码点位信息主键
     * @return 二维码点位信息
     */
    @Override
    public BizQrcodeStore selectBizQrcodeStoreById(Long id)
    {
        return bizQrcodeStoreMapper.selectBizQrcodeStoreById(id);
    }

    /**
     * 查询二维码点位信息列表
     * 
     * @param bizQrcodeStore 二维码点位信息
     * @return 二维码点位信息
     */
    @Override
    public List<BizQrcodeStore> selectBizQrcodeStoreList(BizQrcodeStore bizQrcodeStore)
    {
        return bizQrcodeStoreMapper.selectBizQrcodeStoreList(bizQrcodeStore);
    }

    /**
     * 新增二维码点位信息
     * 
     * @param bizQrcodeStore 二维码点位信息
     * @return 结果
     */
    @Override
    public int insertBizQrcodeStore(BizQrcodeStore bizQrcodeStore)
    {
        bizQrcodeStore.setCreateTime(DateUtils.getNowDate());
        return bizQrcodeStoreMapper.insertBizQrcodeStore(bizQrcodeStore);
    }

    /**
     * 修改二维码点位信息
     * 
     * @param bizQrcodeStore 二维码点位信息
     * @return 结果
     */
    @Override
    public int updateBizQrcodeStore(BizQrcodeStore bizQrcodeStore)
    {
        bizQrcodeStore.setUpdateTime(DateUtils.getNowDate());
        return bizQrcodeStoreMapper.updateBizQrcodeStore(bizQrcodeStore);
    }

    /**
     * 批量删除二维码点位信息
     * 
     * @param ids 需要删除的二维码点位信息主键
     * @return 结果
     */
    @Override
    public int deleteBizQrcodeStoreByIds(Long[] ids)
    {
        return bizQrcodeStoreMapper.deleteBizQrcodeStoreByIds(ids);
    }

    /**
     * 删除二维码点位信息信息
     * 
     * @param id 二维码点位信息主键
     * @return 结果
     */
    @Override
    public int deleteBizQrcodeStoreById(Long id)
    {
        return bizQrcodeStoreMapper.deleteBizQrcodeStoreById(id);
    }
}
