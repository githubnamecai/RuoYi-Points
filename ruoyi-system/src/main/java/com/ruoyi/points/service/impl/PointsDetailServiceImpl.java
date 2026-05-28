package com.ruoyi.points.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.points.domain.PointsDetail;
import com.ruoyi.points.domain.vo.PointsStatVO;
import com.ruoyi.points.mapper.H5UserMapper;
import com.ruoyi.points.mapper.PointsDetailMapper;
import com.ruoyi.points.service.IPointsDetailService;

@Service
public class PointsDetailServiceImpl implements IPointsDetailService
{
    @Autowired private PointsDetailMapper detailMapper;
    @Autowired private H5UserMapper h5UserMapper;

    @Override public List<PointsDetail> selectDetailList(PointsDetail detail) { return detailMapper.selectDetailList(detail); }

    @Override public List<PointsDetail> selectUserDetailList(Long userId) { return detailMapper.selectUserDetailList(userId); }

    @Override
    public int record(Long userId, String changeType, String sourceType, String sourceId,
                      Integer changePoints, Integer balance, String description, String operator)
    {
        PointsDetail d = new PointsDetail();
        d.setUserId(userId);
        d.setChangeType(changeType);
        d.setSourceType(sourceType);
        d.setSourceId(sourceId);
        d.setChangePoints(changePoints);
        d.setBalance(balance);
        d.setDescription(description);
        d.setCreateBy(operator);
        return detailMapper.insert(d);
    }

    @Override
    public PointsStatVO stat()
    {
        PointsStatVO vo = new PointsStatVO();
        vo.setTotalIssued(detailMapper.sumByChangeType("0"));
        vo.setTotalConsumed(detailMapper.sumByChangeType("1"));
        vo.setTotalBalance(h5UserMapper.sumPointsBalance());
        vo.setUserCount(h5UserMapper.countUser());
        vo.setTodayIssued(detailMapper.sumTodayByChangeType("0"));
        vo.setTodayConsumed(detailMapper.sumTodayByChangeType("1"));
        return vo;
    }
}
