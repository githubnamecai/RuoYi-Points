package com.ruoyi.points.token;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.points.constant.PointsConstants;

/**
 * H5 token 存储
 *
 * 采用两个 key：
 * - {@code h5_login_tokens:{uuid}} -> userId：token 本体，TTL 7 天，用于鉴权；
 * - {@code h5:user:tokens:{userId}} -> Set&lt;uuid&gt;：用户维度索引，用于按用户批量下线会话。
 *
 * 说明：token 本体沿用 StringRedisTemplate 的纯字符串存取（与历史数据保持一致），
 * 不要改成 RedisTemplate 的 JSON 序列化，否则存量 token 会读不出来。
 *
 * @author ruoyi
 */
@Component
public class H5TokenStore
{
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * token 有效期（分钟），Redis TTL 与 JWT exp 共用此值
     * 默认 7 天，可通过 application.yml 的 h5.token-expire-minutes 调整
     */
    @Value("${h5.token-expire-minutes:10080}")
    private long expireMinutes;

    public long getExpireMinutes()
    {
        return expireMinutes > 0 ? expireMinutes : PointsConstants.H5_TOKEN_EXPIRE_MINUTES;
    }

    /**
     * 保存 token 并登记到用户索引
     */
    public void save(String uuid, Long userId)
    {
        if (StringUtils.isEmpty(uuid) || userId == null) return;

        redisTemplate.opsForValue().set(PointsConstants.H5_TOKEN_PREFIX + uuid, String.valueOf(userId),
            getExpireMinutes(), TimeUnit.MINUTES);

        String userKey = PointsConstants.H5_USER_TOKENS_PREFIX + userId;
        redisTemplate.opsForSet().add(userKey, uuid);
        // 索引随最后一个 token 一起过期，避免 Set 永久堆积
        redisTemplate.expire(userKey, getExpireMinutes(), TimeUnit.MINUTES);
    }

    /**
     * 根据 uuid 获取用户ID，token 不存在或已过期返回 null
     */
    public Long getUserId(String uuid)
    {
        if (StringUtils.isEmpty(uuid)) return null;
        String value = redisTemplate.opsForValue().get(PointsConstants.H5_TOKEN_PREFIX + uuid);
        if (StringUtils.isEmpty(value)) return null;
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    /**
     * 移除单个 token（退出登录）
     */
    public void remove(String uuid, Long userId)
    {
        if (StringUtils.isEmpty(uuid)) return;
        redisTemplate.delete(PointsConstants.H5_TOKEN_PREFIX + uuid);
        if (userId != null)
        {
            redisTemplate.opsForSet().remove(PointsConstants.H5_USER_TOKENS_PREFIX + userId, uuid);
        }
    }

    /**
     * 下线某用户的全部会话（改密、重置密码、冻结账号等场景）
     *
     * @return 本次清理的 token 数量
     */
    public int removeAllOfUser(Long userId)
    {
        if (userId == null) return 0;

        String userKey = PointsConstants.H5_USER_TOKENS_PREFIX + userId;
        Set<String> uuids = redisTemplate.opsForSet().members(userKey);
        if (uuids == null || uuids.isEmpty())
        {
            redisTemplate.delete(userKey);
            return 0;
        }
        for (String uuid : uuids)
        {
            redisTemplate.delete(PointsConstants.H5_TOKEN_PREFIX + uuid);
        }
        redisTemplate.delete(userKey);
        return uuids.size();
    }
}
