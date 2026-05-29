package com.ruoyi.points.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.points.domain.H5User;

/**
 * H5 用户 Mapper
 */
public interface H5UserMapper
{
    List<H5User> selectH5UserList(H5User user);

    H5User selectUserById(Long userId);

    H5User selectUserByPhone(String phone);

    int insertUser(H5User user);

    int updateUser(H5User user);

    /**
     * 乐观锁扣减积分：
     * update t_user set points_balance=points_balance-#{points},
     *   total_consumed=total_consumed+#{points}
     * where user_id=#{userId} and points_balance>=#{points} and status='0'
     */
    int decreasePoints(@Param("userId") Long userId, @Param("points") Integer points);

    /** 增加积分 */
    int increasePoints(@Param("userId") Long userId, @Param("points") Integer points);

    /** 更新签到信息 */
    int updateSignInfo(@Param("userId") Long userId,
                       @Param("continuousDays") Integer continuousDays,
                       @Param("lastSignDate") String lastSignDate);

    /** 修改状态(冻结/解冻) */
    int updateStatus(@Param("userId") Long userId, @Param("status") String status);

    /** 平台统计 */
    Long sumPointsBalance();
    Long countUser();

    // 批量删除用户
    int deleteUserByIds(Long[] ids);
}
