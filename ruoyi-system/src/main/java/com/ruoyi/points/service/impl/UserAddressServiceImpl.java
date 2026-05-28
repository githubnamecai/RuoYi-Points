package com.ruoyi.points.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.points.domain.UserAddress;
import com.ruoyi.points.mapper.UserAddressMapper;
import com.ruoyi.points.service.IUserAddressService;

@Service
public class UserAddressServiceImpl implements IUserAddressService
{
    @Autowired
    private UserAddressMapper addressMapper;

    @Override public List<UserAddress> listByUser(Long userId) { return addressMapper.selectByUserId(userId); }
    @Override public UserAddress getById(Long addressId) { return addressMapper.selectById(addressId); }
    @Override public UserAddress getDefault(Long userId) { return addressMapper.selectDefault(userId); }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(UserAddress address)
    {
        if ("1".equals(address.getIsDefault())) addressMapper.clearDefault(address.getUserId());
        return addressMapper.insert(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UserAddress address)
    {
        if ("1".equals(address.getIsDefault())) addressMapper.clearDefault(address.getUserId());
        return addressMapper.update(address);
    }

    @Override
    public int delete(Long addressId, Long userId) { return addressMapper.deleteById(addressId, userId); }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setDefault(Long addressId, Long userId)
    {
        addressMapper.clearDefault(userId);
        UserAddress a = new UserAddress();
        a.setAddressId(addressId);
        a.setUserId(userId);
        a.setIsDefault("1");
        return addressMapper.update(a);
    }
}
