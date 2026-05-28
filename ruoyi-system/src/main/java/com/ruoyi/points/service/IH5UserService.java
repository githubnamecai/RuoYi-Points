package com.ruoyi.points.service;

import java.util.List;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.domain.dto.PointsAdjustDTO;

public interface IH5UserService
{
    List<H5User> selectH5UserList(H5User user);
    H5User selectUserById(Long userId);
    H5User selectUserByPhone(String phone);

    /** 首次登录自动注册 */
    H5User registerOrGet(String phone, String loginIp);

    int updateUser(H5User user);
    int changeStatus(Long userId, String status);

    /** 管理员手动调整积分（增/减） */
    int adjustPoints(PointsAdjustDTO dto, String operator);
}
