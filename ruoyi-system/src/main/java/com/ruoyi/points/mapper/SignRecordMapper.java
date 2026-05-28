package com.ruoyi.points.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.points.domain.SignRecord;

public interface SignRecordMapper
{
    /** 查询某用户某日记录（用于幂等校验） */
    SignRecord selectByUserAndDate(@Param("userId") Long userId, @Param("signDate") Date signDate);

    /** 查询某用户某月份的签到记录 */
    List<SignRecord> selectMonthRecords(@Param("userId") Long userId,
                                        @Param("year") int year,
                                        @Param("month") int month);

    /** 查询该用户最近一次签到 */
    SignRecord selectLastRecord(Long userId);

    int insert(SignRecord record);
}
