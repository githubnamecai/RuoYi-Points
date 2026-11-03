package com.ruoyi.points.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.PasswordPolicy;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.points.constant.PointsConstants;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.service.IH5LoginService;
import com.ruoyi.points.service.IH5UserService;
import com.ruoyi.points.token.H5TokenStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;

/**
 * H5 登录/Token 服务
 *
 * 使用与 RuoYi 后台一致的 JWT 算法（HMAC-SHA），密钥取自 token.secret 配置；
 * 但缓存命名空间使用 h5_login_tokens: 与后台用户隔离，保证安全。
 */
@Service
public class H5LoginServiceImpl implements IH5LoginService
{
    private static final Logger log = LoggerFactory.getLogger(H5LoginServiceImpl.class);
    private static final String UUID_CLAIM = "h5_uid";
    private static final String USER_ID_CLAIM = "userId";

    @Value("${token.secret}")
    private String secret;

    /** 密码登录：同一 IP 每分钟最大尝试次数 */
    @Value("${h5.login-ip-max-per-minute:20}")
    private int ipMaxPerMinute;

    /** 密码登录失败最大次数（复用后台登录同名配置） */
    @Value("${user.password.maxRetryCount:5}")
    private int maxRetryCount;

    /** 密码登录失败锁定时长（分钟） */
    @Value("${user.password.lockTime:10}")
    private int lockTime;

    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private IH5UserService h5UserService;
    @Autowired private H5TokenStore tokenStore;

    @Override
    public String loginByPassword(String phone, String password, String loginIp)
    {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password))
            throw new ServiceException("参数不完整");

        // 1. 同 IP 限流：防止用同一出口批量轮询多个手机号
        checkIpRateLimit(loginIp);
        // 2. 同手机号失败次数锁定：防止对单个账号暴力破解
        checkPasswordFailLimit(phone);

        H5User user = h5UserService.selectUserByPhone(phone);
        if (user == null)
            throw new ServiceException("账号不存在");
        if ("1".equals(user.getStatus()))
            throw new ServiceException("账号已被冻结");
        if (StringUtils.isEmpty(user.getPassword()))
            throw new ServiceException("该账号未设置密码");
        if (!SecurityUtils.matchesPassword(password, user.getPassword()))
        {
            recordPasswordFail(phone);
            throw new ServiceException("密码错误");
        }
        clearPasswordFail(phone);

        H5User update = new H5User();
        update.setUserId(user.getUserId());
        update.setLastLoginTime(new java.util.Date());
        update.setLastLoginIp(loginIp);
        h5UserService.updateUser(update);

        return createToken(user);
    }

    /**
     * 同一 IP 每分钟的密码登录尝试次数限流
     *
     * 阈值取配置 h5.login-ip-max-per-minute（默认 20）。比单账号阈值宽松，
     * 避免公司/校园等 NAT 出口的正常用户被互相牵连。
     */
    private void checkIpRateLimit(String loginIp)
    {
        if (StringUtils.isEmpty(loginIp)) return;

        String key = PointsConstants.H5_LOGIN_IP_RATE_PREFIX + loginIp;
        Long count = redisTemplate.opsForValue().increment(key);
        ensureExpire(key, 1, TimeUnit.MINUTES);
        if (count != null && count > ipMaxPerMinute)
        {
            throw new ServiceException("登录尝试过于频繁，请稍后再试");
        }
    }

    /** 密码登录失败次数缓存 key（按手机号维度） */
    private String loginFailKey(String phone)
    {
        return PointsConstants.H5_LOGIN_FAIL_PREFIX + phone;
    }

    /**
     * 失败次数超限则直接拒绝，不再执行密码比对
     */
    private void checkPasswordFailLimit(String phone)
    {
        String value = redisTemplate.opsForValue().get(loginFailKey(phone));
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
            throw new ServiceException("密码错误次数过多，请" + lockTime + "分钟后再试");
        }
    }

    /**
     * 累计一次密码登录失败次数
     */
    private void recordPasswordFail(String phone)
    {
        String key = loginFailKey(phone);
        Long count = redisTemplate.opsForValue().increment(key);
        ensureExpire(key, lockTime, TimeUnit.MINUTES);
        log.info("H5 密码登录失败: phone={}, 当前失败次数={}", phone, count);
    }

    /**
     * 登录成功后清空失败次数
     */
    private void clearPasswordFail(String phone)
    {
        redisTemplate.delete(loginFailKey(phone));
    }

    /**
     * 保证计数键一定带过期时间，避免 INCR 生成的键无 TTL 导致永久锁定
     */
    private void ensureExpire(String key, long timeout, TimeUnit unit)
    {
        Long ttl = redisTemplate.getExpire(key);
        if (ttl == null || ttl < 0)
        {
            redisTemplate.expire(key, timeout, unit);
        }
    }

    @Override
    public void register(String phone, String password, String loginIp)
    {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password))
            throw new ServiceException("参数不完整");

        // 密码复杂度校验：至少8位，包含大小写字母和数字（与修改密码共用同一策略）
        String policyError = PasswordPolicy.check(password);
        if (policyError != null)
            throw new ServiceException(policyError);

        // 检查手机号是否已注册
        H5User exist = h5UserService.selectUserByPhone(phone);
        if (exist != null)
            throw new ServiceException("该手机号已注册");

        // 创建新用户
        H5User user = new H5User();
        user.setPhone(phone);
        user.setPassword(password);
        user.setNickname("用户" + phone.substring(phone.length() - 4));
        user.setRegisterTime(new java.util.Date());
        user.setLastLoginIp(loginIp);
        user.setLastLoginTime(new java.util.Date());
        user.setStatus("0");
        user.setPointsBalance(0);
        h5UserService.insertUser(user);
    }

    private String createToken(H5User user)
    {
        // 生成 token
        String uuid = IdUtils.fastSimpleUUID();
        Map<String, Object> claims = new HashMap<>();
        claims.put(UUID_CLAIM, uuid);
        claims.put(USER_ID_CLAIM, user.getUserId());
        // 替换成这个（0.9.1 标准写法）
        // JWT 自带 exp，与 Redis 缓存 TTL 对齐（同一份配置 h5.token-expire-minutes）
        long now = System.currentTimeMillis();
        long expireMillis = tokenStore.getExpireMinutes() * 60L * 1000L;
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expireMillis))
                .signWith(SignatureAlgorithm.HS256, buildKey()) // 这里加算法
                .compact();

        // 缓存用户信息（同时登记用户维度索引，便于改密后批量下线）
        tokenStore.save(uuid, user.getUserId());
        return jwt;
    }

    @Override
    public H5User getLoginUser(String token)
    {
        if (StringUtils.isEmpty(token)) return null;
        try
        {
            Claims c = Jwts.parser()
                    .setSigningKey(buildKey())
                    .parseClaimsJws(token).getBody();
            String uuid = (String) c.get(UUID_CLAIM);
            Long userId = tokenStore.getUserId(uuid);
            if (userId == null) return null;
            return h5UserService.selectUserById(userId);
        }
        catch (Exception e)
        {
            log.debug("H5 token 解析失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void logout(String token)
    {
        if (StringUtils.isEmpty(token)) return;
        try
        {
            // 解析（parser）替换0.91版本语法

            Claims c = Jwts.parser().setSigningKey(buildKey()).parseClaimsJws(token).getBody();
            String uuid = (String) c.get(UUID_CLAIM);
            Object userIdClaim = c.get(USER_ID_CLAIM);
            Long userId = userIdClaim == null ? null : Long.valueOf(userIdClaim.toString());
            tokenStore.remove(uuid, userId);
        }
        catch (Exception ignored) {}
    }

    private SecretKey buildKey()
    {
        // 与 RuoYi TokenService 类似：直接用 secret 字节作为 HMAC 密钥
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        // 不足长度时填充
        if (bytes.length < 32)
        {
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            bytes = padded;
        }
        // 用字符串 / 字节数组当密钥替换0.91版本
        SecretKey key = new SecretKeySpec(bytes, SignatureAlgorithm.HS256.getJcaName());
        return key;
    }
}
