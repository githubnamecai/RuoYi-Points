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

    int insertUser(H5User user);
    int updateUser(H5User user);
    // ... 已有方法
    int resetPassword(Long userId, String newPassword);

    /**
     * H5 用户自助修改密码（校验原密码 + 新密码复杂度）
     *
     * @param userId 当前登录用户ID
     * @param oldPassword 原密码（明文）
     * @param newPassword 新密码（明文）
     * @return 影响行数
     */
    int changePassword(Long userId, String oldPassword, String newPassword);

    int deleteUserByIds(Long[] userIds);
    int changeStatus(Long userId, String status);

    /** 管理员手动调整积分（增/减） */
    int adjustPoints(PointsAdjustDTO dto, String operator);
}
