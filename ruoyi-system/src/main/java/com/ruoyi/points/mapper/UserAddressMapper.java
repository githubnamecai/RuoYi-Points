package com.ruoyi.points.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.points.domain.UserAddress;

public interface UserAddressMapper
{
    List<UserAddress> selectByUserId(Long userId);
    UserAddress selectById(Long addressId);
    UserAddress selectDefault(Long userId);
    int insert(UserAddress address);
    int update(UserAddress address);
    int deleteById(@Param("addressId") Long addressId, @Param("userId") Long userId);
    /** 取消其他默认 */
    int clearDefault(Long userId);
}
