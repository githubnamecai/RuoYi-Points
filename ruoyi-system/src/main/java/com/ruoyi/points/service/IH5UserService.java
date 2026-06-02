package com.ruoyi.points.service;

import java.util.List;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.domain.ResetPwdDTO;
import com.ruoyi.points.domain.dto.PointsAdjustDTO;

public interface IH5UserService
{
    List<H5User> selectH5UserList(H5User user);
    H5User selectUserById(Long userId);
    H5User selectUserByPhone(String phone);

    /** 首次登录自动注册 */
    H5User registerOrGet(String phone, String loginIp);

    int insertUser(H5User user);
    int updateUser(H5User user);
    // ... 已有方法
    int resetPassword(Long userId, String newPassword);
    int deleteUserByIds(Long[] userIds);
    int changeStatus(Long userId, String status);

    /** 管理员手动调整积分（增/减） */
    int adjustPoints(PointsAdjustDTO dto, String operator);
}
