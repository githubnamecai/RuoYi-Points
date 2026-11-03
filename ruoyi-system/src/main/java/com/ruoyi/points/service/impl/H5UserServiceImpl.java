package com.ruoyi.points.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.ruoyi.points.domain.ResetPwdDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.PasswordPolicy;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.crypto.SensitiveCryptoUtils;
import com.ruoyi.points.constant.PointsConstants;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.domain.dto.PointsAdjustDTO;
import com.ruoyi.points.mapper.H5UserMapper;
import com.ruoyi.points.service.IH5UserService;
import com.ruoyi.points.service.IPointsDetailService;
import com.ruoyi.points.token.H5TokenStore;

@Service
public class H5UserServiceImpl implements IH5UserService
{
    private static final Logger log = LoggerFactory.getLogger(H5UserServiceImpl.class);

    @Autowired
    private H5UserMapper h5UserMapper;

    @Autowired
    private IPointsDetailService pointsDetailService;

    @Autowired
    private H5TokenStore tokenStore;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /** 原密码最大错误次数（复用后台登录的同名配置） */
    @Value("${user.password.maxRetryCount:5}")
    private int maxRetryCount;

    /** 原密码错误锁定时长（分钟） */
    @Value("${user.password.lockTime:10}")
    private int lockTime;

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

    /**
     * 重置密码（后台管理员重置 / 自助改密的最终落库方法）
     *
     * 统一在此处做密码复杂度校验，保证「后台重置」也无法设置弱密码；
     * 重置成功后强制下线该用户全部 H5 会话，避免旧 token 继续可用。
     */
    @Override
    public int resetPassword(Long userId, String newPassword)
    {
        if (userId == null) throw new ServiceException("用户不存在");

        String policyError = PasswordPolicy.check(newPassword);
        if (policyError != null) throw new ServiceException(policyError);

        int rows = h5UserMapper.resetPassword(userId, SecurityUtils.encryptPassword(newPassword));
        if (rows > 0)
        {
            int kicked = tokenStore.removeAllOfUser(userId);
            log.info("用户 {} 密码已更新，已下线 {} 个 H5 会话", userId, kicked);
        }
        return rows;
    }

    /**
     * H5 用户自助修改密码：
     * - 必须校验原密码，防止 token 被窃取后直接改密接管账号；
     * - 原密码连续错误按 {@code user.password.maxRetryCount} 次锁定 {@code user.password.lockTime} 分钟；
     * - 新密码不允许与原密码相同；
     * - 复杂度校验、BCrypt 加密与旧会话下线由 {@link #resetPassword} 统一处理。
     */
    @Override
    public int changePassword(Long userId, String oldPassword, String newPassword)
    {
        if (userId == null) throw new ServiceException("用户未登录");
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword))
            throw new ServiceException("参数不完整");

        // 0. 先判断是否已因原密码错误次数过多被锁定
        checkPasswordRetryLimit(userId);

        H5User user = h5UserMapper.selectUserById(userId);
        if (user == null) throw new ServiceException("用户不存在");
        if (StringUtils.isEmpty(user.getPassword()))
            throw new ServiceException("该账号未设置密码，请先通过短信登录设置密码");

        // 1. 校验原密码，失败则累计次数
        if (!SecurityUtils.matchesPassword(oldPassword, user.getPassword()))
        {
            recordPasswordRetry(userId);
            throw new ServiceException("原密码错误");
        }

        // 2. 新密码不能与原密码相同
        if (SecurityUtils.matchesPassword(newPassword, user.getPassword()))
            throw new ServiceException("新密码不能与原密码相同");

        // 3. 落库（内部含复杂度校验 + BCrypt 加密 + 下线全部会话）
        int rows = resetPassword(userId, newPassword);
        clearPasswordRetry(userId);
        return rows;
    }

    /** 原密码错误次数缓存 key（按用户维度） */
    private String passwordRetryKey(Long userId)
    {
        return PointsConstants.H5_PWD_RETRY_PREFIX + userId;
    }

    /**
     * 原密码错误次数超限则直接拒绝，避免持有 token 的攻击者暴力尝试原密码
     */
    private void checkPasswordRetryLimit(Long userId)
    {
        String value = redisTemplate.opsForValue().get(passwordRetryKey(userId));
        if (StringUtils.isEmpty(value)) return;

        int count;
        try
        {
            count = Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return;
        }
        if (count >= maxRetryCount)
        {
            throw new ServiceException("原密码错误次数过多，请" + lockTime + "分钟后再试");
        }
    }

    /**
     * 累计一次原密码错误次数
     *
     * 采用固定窗口：计数键首次创建时设置过期时间，之后只累加不再刷新，
     * 即「从第一次失败起算 lockTime 分钟」。同时兜底处理 INCR 产生的无 TTL 键，
     * 避免计数永久残留导致账号被永久锁定。
     */
    private void recordPasswordRetry(Long userId)
    {
        String key = passwordRetryKey(userId);
        Long count = redisTemplate.opsForValue().increment(key);
        Long ttl = redisTemplate.getExpire(key);
        if (ttl == null || ttl < 0)
        {
            redisTemplate.expire(key, lockTime, TimeUnit.MINUTES);
        }
        log.info("用户 {} 原密码校验失败，当前失败次数 {}", userId, count);
    }

    /**
     * 改密成功后清空错误次数
     */
    private void clearPasswordRetry(Long userId)
    {
        redisTemplate.delete(passwordRetryKey(userId));
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
