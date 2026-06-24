package com.ruoyi.qrcode.service;

import java.util.List;
import com.ruoyi.qrcode.domain.BizQrcodeStore;

/**
 * 二维码点位信息Service接口
 * 
 * @author ruoyi
 * @date 2026-06-24
 */
public interface IBizQrcodeStoreService 
{
    /**
     * 查询二维码点位信息
     * 
     * @param id 二维码点位信息主键
     * @return 二维码点位信息
     */
    public BizQrcodeStore selectBizQrcodeStoreById(Long id);

    /**
     * 查询二维码点位信息列表
     * 
     * @param bizQrcodeStore 二维码点位信息
     * @return 二维码点位信息集合
     */
    public List<BizQrcodeStore> selectBizQrcodeStoreList(BizQrcodeStore bizQrcodeStore);

    /**
     * 新增二维码点位信息
     * 
     * @param bizQrcodeStore 二维码点位信息
     * @return 结果
     */
    public int insertBizQrcodeStore(BizQrcodeStore bizQrcodeStore);

    /**
     * 修改二维码点位信息
     * 
     * @param bizQrcodeStore 二维码点位信息
     * @return 结果
     */
    public int updateBizQrcodeStore(BizQrcodeStore bizQrcodeStore);

    /**
     * 批量删除二维码点位信息
     * 
     * @param ids 需要删除的二维码点位信息主键集合
     * @return 结果
     */
    public int deleteBizQrcodeStoreByIds(Long[] ids);

    /**
     * 删除二维码点位信息信息
     * 
     * @param id 二维码点位信息主键
     * @return 结果
     */
    public int deleteBizQrcodeStoreById(Long id);
}
