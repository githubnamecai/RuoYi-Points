package com.ruoyi.qrcode.service.impl;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.qrcode.domain.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.qrcode.mapper.BizQrcodeStoreMapper;
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

    /**
     * 下载图片压缩包
     * @param ids
     * @param response
     */
    @Override
    public void downloadImagesAsZip(Long[] ids, HttpServletResponse response)
    {
        List<BizQrcodeStore> families = selectBizQrcodeStoreList(new BizQrcodeStore());
        List<UtilDownloadZip> imagesToDownload = new ArrayList<>();
        int num = 0;

        for (BizQrcodeStore family : families) {
            if (family.getQrcodeUrl() == null) {
                continue;
            }
            UtilDownloadZip utilDownloadZip = new UtilDownloadZip();
//            utilDownloadZip.setPath(Paths.get(RuoYiConfig.getUploadPath()+family.getQrcodeUrl().split("profile/upload")[1]));
            String qrcodeUrl = family.getQrcodeUrl();
            String subPath = "";
            String targetPrefix = "profile/upload";

            if (qrcodeUrl != null && qrcodeUrl.contains(targetPrefix)) {
                int index = qrcodeUrl.indexOf(targetPrefix);
                // 截取 profile/upload 后面的所有内容
                subPath = qrcodeUrl.substring(index + targetPrefix.length());
                // 去除开头多余的 /，避免路径出现双斜杠 upload//qrcode/qrc
                if (subPath.startsWith("/")) {
                    subPath = subPath.substring(1);
                }
            }

// Paths.get 多参数自动拼接分隔符，不会出现 //
            utilDownloadZip.setPath(Paths.get(RuoYiConfig.getUploadPath(), subPath));
            utilDownloadZip.setName(family.getName()+"二维码.png");
            imagesToDownload.add(utilDownloadZip);
            num++;
        }
//        System.out.println("共导出了"+num+"张二维码");
        DownloadZip.downloadImagesAsZipNew(imagesToDownload, "familyQrcode", response);

    }

    @Override
    public int updateQRcode(BizQrcodeStore bizQrcodeStore)
    {
        createQrcode(bizQrcodeStore);
        return 1;
    }
    @Override
    public int updateQRcodeByIds(Long[] ids)
    {
        List<BizQrcodeStore> families = selectBizQrcodeStoreList(new BizQrcodeStore());
        // 生成二维码图片
        for (BizQrcodeStore family : families) {
            if (Objects.equals(family.getId(), ids[0])) {
                createQrcode(family);
            }
        }
        return 1;
    }



    public void createQrcode(BizQrcodeStore bizQrcodeStore)
    {
        UtilQrcode utilQrcode = new UtilQrcode();
        utilQrcode.setUrl("https://gift.jsocn.com/h5/#/home?Id="+bizQrcodeStore.getId());

        utilQrcode.setMsg(bizQrcodeStore.getName());
        // 生成二维码，并更新dept数据表
        bizQrcodeStore.setQrPhoto(Qrcode.createQrcode(utilQrcode));
        bizQrcodeStore.setQrcodeUrl(utilQrcode.getUrl());
//        System.out.println("二维码生成成功，二维码图片路径为：" + bizQrcodeStore.getQrPhoto());
        bizQrcodeStoreMapper.updateBizQrcodeStore(bizQrcodeStore);
    }
}
