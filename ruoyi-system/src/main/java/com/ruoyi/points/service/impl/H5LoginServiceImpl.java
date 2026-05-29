package com.ruoyi.points.service.impl;

import java.nio.charset.StandardCharsets;
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
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.points.constant.PointsConstants;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.service.IH5LoginService;
import com.ruoyi.points.service.IH5UserService;
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

    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private IH5UserService h5UserService;

    @Override
    public void sendSmsCode(String phone)
    {
        if (StringUtils.isEmpty(phone)) throw new ServiceException("手机号不能为空");
        // Mock：随机 6 位验证码 -> Redis（5 分钟过期）
        String code = String.format("%06d", (int) (Math.random() * 1000000));
        redisTemplate.opsForValue().set(PointsConstants.H5_SMS_CODE_PREFIX + phone, code,
            PointsConstants.SMS_EXPIRE_MINUTES, TimeUnit.MINUTES);
        // 真实环境对接短信服务商，此处仅打印日志
        log.info("[SMS-MOCK] 向手机号 {} 发送验证码: {}（5分钟有效）", phone, code);
    }

    @Override
    public String loginByCode(String phone, String code, String loginIp)
    {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code))
            throw new ServiceException("参数不完整");
        String key = PointsConstants.H5_SMS_CODE_PREFIX + phone;
        String real = redisTemplate.opsForValue().get(key);
        // 测试便利：万能码 123456 始终有效
        if (!"123456".equals(code) && !code.equals(real))
            throw new ServiceException("验证码错误或已过期");
        redisTemplate.delete(key);

        H5User user = h5UserService.registerOrGet(phone, loginIp);
        return createToken(user);
    }

    @Override
    public String loginByPassword(String phone, String password, String loginIp)
    {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password))
            throw new ServiceException("参数不完整");

        H5User user = h5UserService.selectUserByPhone(phone);
        if (user == null)
            throw new ServiceException("账号不存在");
        if ("1".equals(user.getStatus()))
            throw new ServiceException("账号已被冻结");
        if (!password.equals(user.getPassword()))
            throw new ServiceException("密码错误");

        H5User update = new H5User();
        update.setUserId(user.getUserId());
        update.setLastLoginTime(new java.util.Date());
        update.setLastLoginIp(loginIp);
        h5UserService.updateUser(update);

        return createToken(user);
    }

    private String createToken(H5User user)
    {
        // 生成 token
        String uuid = IdUtils.fastSimpleUUID();
        Map<String, Object> claims = new HashMap<>();
        claims.put(UUID_CLAIM, uuid);
        claims.put(USER_ID_CLAIM, user.getUserId());
        // 替换成这个（0.9.1 标准写法）
        String jwt = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, buildKey()) // 这里加算法
                .compact();

        // 缓存用户信息
        redisTemplate.opsForValue().set(PointsConstants.H5_TOKEN_PREFIX + uuid,
            user.getUserId().toString(),
            PointsConstants.H5_TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
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
            String userIdStr = redisTemplate.opsForValue().get(PointsConstants.H5_TOKEN_PREFIX + uuid);
            if (StringUtils.isEmpty(userIdStr)) return null;
            return h5UserService.selectUserById(Long.parseLong(userIdStr));
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
            if (uuid != null) redisTemplate.delete(PointsConstants.H5_TOKEN_PREFIX + uuid);
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
