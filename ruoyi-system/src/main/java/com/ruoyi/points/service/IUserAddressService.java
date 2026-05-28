package com.ruoyi.points.service;

import java.util.List;
import com.ruoyi.points.domain.UserAddress;

public interface IUserAddressService
{
    List<UserAddress> listByUser(Long userId);
    UserAddress getById(Long addressId);
    UserAddress getDefault(Long userId);
    int add(UserAddress address);
    int update(UserAddress address);
    int delete(Long addressId, Long userId);
    int setDefault(Long addressId, Long userId);
}
