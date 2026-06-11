package com.ruoyi.points.service.impl;

import java.util.List;
import java.util.regex.Pattern;

import com.ruoyi.points.domain.ResetPwdDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.crypto.SensitiveCryptoUtils;
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
    public List<H5User> selectH5UserList(H5User user)
    {
        prepareQueryHash(user);
        List<H5User> list = h5UserMapper.selectH5UserList(user);
        decryptSensitiveForReturn(list);
        return list;
    }

    @Override
    public H5User selectUserById(Long userId)
    {
        H5User user = h5UserMapper.selectUserById(userId);
        return decryptSensitiveForReturn(user);
    }

    @Override
    public H5User selectUserByPhone(String phone)
    {
        if (StringUtils.isEmpty(phone)) return null;
        String phoneHash = SensitiveCryptoUtils.sha256Hex(phone);
        H5User user = h5UserMapper.selectUserByPhoneHash(phoneHash);
        return decryptSensitiveForReturn(user);
    }

    @Override
    public H5User registerOrGet(String phone, String loginIp)
    {
        H5User user = selectUserByPhone(phone);
        if (user == null)
        {
            user = new H5User();
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(7));
            user.setPointsBalance(0);
            user.setStatus("0");
            user.setLastLoginIp(loginIp);
            insertUser(user);
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
        return selectUserById(user.getUserId());
    }

    @Override
    public int insertUser(H5User user)
    {
        if (StringUtils.isEmpty(user.getStatus())) user.setStatus("0");
        if (user.getPointsBalance() == null) user.setPointsBalance(0);
        prepareSensitiveForSave(user);
        return h5UserMapper.insertUser(user);
    }

    @Override
    public int updateUser(H5User user)
    {
        prepareSensitiveForSave(user);
        return h5UserMapper.updateUser(user);
    }

    @Override
    public int resetPassword(Long userId, String newPassword)
    {
        if (StringUtils.isEmpty(newPassword)) throw new ServiceException("密码不能为空");
        return h5UserMapper.resetPassword(userId, SecurityUtils.encryptPassword(newPassword));
    }

    @Override
    public int deleteUserByIds(Long[] userIds) { return h5UserMapper.deleteUserByIds(userIds); }

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

    /**
     * 对查询条件中的手机号/身份证进行 Hash 预处理，支持等值检索。
     */
    private void prepareQueryHash(H5User query)
    {
        if (query == null) return;
        if (StringUtils.isNotEmpty(query.getPhone()))
        {
            query.setPhoneHash(SensitiveCryptoUtils.sha256Hex(query.getPhone()));
        }
        if (StringUtils.isNotEmpty(query.getIdNumber()))
        {
            query.setIdNumberHash(SensitiveCryptoUtils.sha256Hex(query.getIdNumber()));
        }
    }

    /**
     * 保存前对敏感字段进行加密/哈希处理（用于数据库存储与检索）。
     */
    private void prepareSensitiveForSave(H5User user)
    {
        if (user == null) return;

        if (StringUtils.isNotEmpty(user.getPhone()))
        {
            String plainPhone = SensitiveCryptoUtils.decrypt(user.getPhone());
            user.setPhoneHash(SensitiveCryptoUtils.sha256Hex(plainPhone));
            user.setPhone(SensitiveCryptoUtils.encrypt(plainPhone));
        }

        if (StringUtils.isNotEmpty(user.getIdNumber()))
        {
            String plainId = SensitiveCryptoUtils.decrypt(user.getIdNumber());
            user.setIdNumberHash(SensitiveCryptoUtils.sha256Hex(plainId));
            user.setIdNumber(SensitiveCryptoUtils.encrypt(plainId));
        }

        if (StringUtils.isNotEmpty(user.getPassword()) && !isBcryptHash(user.getPassword()))
        {
            user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        }
    }

    /**
     * 返回前对敏感字段进行解密，让脱敏序列化器按明文规则输出（仅输出脱敏后的结果）。
     */
    private H5User decryptSensitiveForReturn(H5User user)
    {
        if (user == null) return null;
        if (StringUtils.isNotEmpty(user.getPhone()))
        {
            user.setPhone(SensitiveCryptoUtils.decrypt(user.getPhone()));
        }
        if (StringUtils.isNotEmpty(user.getIdNumber()))
        {
            user.setIdNumber(SensitiveCryptoUtils.decrypt(user.getIdNumber()));
        }
        return user;
    }

    private void decryptSensitiveForReturn(List<H5User> list)
    {
        if (list == null || list.isEmpty()) return;
        for (H5User user : list)
        {
            decryptSensitiveForReturn(user);
        }
    }

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$\\d\\d\\$.*");

    /**
     * 判断字符串是否为 BCrypt 密文（避免重复加密）。
     */
    private boolean isBcryptHash(String value)
    {
        if (StringUtils.isEmpty(value)) return false;
        return BCRYPT_PATTERN.matcher(value).matches();
    }
}
