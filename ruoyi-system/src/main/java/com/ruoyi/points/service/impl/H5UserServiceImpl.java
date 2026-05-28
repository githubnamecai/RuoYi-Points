package com.ruoyi.points.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.points.constant.PointsConstants;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.domain.dto.PointsAdjustDTO;
import com.ruoyi.points.mapper.H5UserMapper;
import com.ruoyi.points.service.IH5UserService;
import com.ruoyi.points.service.IPointsDetailService;

@Service
public class H5UserServiceImpl implements IH5UserService
{
    @Autowired
    private H5UserMapper h5UserMapper;

    @Autowired
    private IPointsDetailService pointsDetailService;

    @Override
    public List<H5User> selectH5UserList(H5User user) { return h5UserMapper.selectH5UserList(user); }

    @Override
    public H5User selectUserById(Long userId) { return h5UserMapper.selectUserById(userId); }

    @Override
    public H5User selectUserByPhone(String phone) { return h5UserMapper.selectUserByPhone(phone); }

    @Override
    public H5User registerOrGet(String phone, String loginIp)
    {
        H5User user = h5UserMapper.selectUserByPhone(phone);
        if (user == null)
        {
            user = new H5User();
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(7));
            user.setPointsBalance(0);
            user.setStatus("0");
            user.setLastLoginIp(loginIp);
            h5UserMapper.insertUser(user);
        }
        else
        {
            if ("1".equals(user.getStatus())) throw new ServiceException("账号已被冻结");
            H5User update = new H5User();
            update.setUserId(user.getUserId());
            update.setLastLoginTime(new java.util.Date());
            update.setLastLoginIp(loginIp);
            h5UserMapper.updateUser(update);
        }
        return h5UserMapper.selectUserById(user.getUserId());
    }

    @Override
    public int updateUser(H5User user) { return h5UserMapper.updateUser(user); }

    @Override
    public int changeStatus(Long userId, String status) { return h5UserMapper.updateStatus(userId, status); }

    /**
     * 管理员调整积分：
     * - 0增加：直接增加，记录明细
     * - 1扣减：使用乐观锁 SQL 校验余额，记录明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int adjustPoints(PointsAdjustDTO dto, String operator)
    {
        H5User user = h5UserMapper.selectUserById(dto.getUserId());
        if (user == null) throw new ServiceException("用户不存在");

        int rows;
        if (PointsConstants.CHANGE_TYPE_INCREASE.equals(dto.getChangeType()))
        {
            rows = h5UserMapper.increasePoints(dto.getUserId(), dto.getPoints());
            if (rows <= 0) throw new ServiceException("积分增加失败");
            Integer balance = user.getPointsBalance() + dto.getPoints();
            pointsDetailService.record(dto.getUserId(),
                PointsConstants.CHANGE_TYPE_INCREASE,
                PointsConstants.SOURCE_ADMIN_ADJUST,
                null, dto.getPoints(), balance,
                StringUtils.isNotEmpty(dto.getReason()) ? dto.getReason() : "后台增加积分",
                operator);
        }
        else
        {
            rows = h5UserMapper.decreasePoints(dto.getUserId(), dto.getPoints());
            if (rows <= 0) throw new ServiceException("积分不足或扣减失败");
            Integer balance = user.getPointsBalance() - dto.getPoints();
            pointsDetailService.record(dto.getUserId(),
                PointsConstants.CHANGE_TYPE_DECREASE,
                PointsConstants.SOURCE_ADMIN_ADJUST,
                null, dto.getPoints(), balance,
                StringUtils.isNotEmpty(dto.getReason()) ? dto.getReason() : "后台扣减积分",
                operator);
        }
        return rows;
    }
}
