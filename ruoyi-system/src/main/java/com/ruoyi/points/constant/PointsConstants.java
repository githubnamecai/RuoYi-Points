package com.ruoyi.points.constant;

/**
 * 积分平台常量
 *
 * @author ruoyi-points
 */
public class PointsConstants
{
    /** H5 用户 LoginUser 缓存 key 前缀（与 RuoYi token 体系隔离，但复用相同的 JWT/Redis 机制） */
    public static final String H5_TOKEN_PREFIX = "h5_login_tokens:";

    /** H5 短信验证码 Redis key 前缀 */
    public static final String H5_SMS_CODE_PREFIX = "h5:sms:code:";

    /** H5 兑换分布式锁 key 前缀 */
    public static final String H5_EXCHANGE_LOCK_PREFIX = "exchange:lock:";

    /** H5 签到限流 key 前缀 */
    public static final String H5_SIGN_RATE_LIMIT_PREFIX = "h5:sign:rate:";

    /** 商品库存 Redis key 前缀（预减库存） */
    public static final String GOODS_STOCK_PREFIX = "points:stock:";

    /** 验证码默认 5 分钟有效期 */
    public static final long SMS_EXPIRE_MINUTES = 5L;

    /** H5 token 有效期（分钟） */
    public static final long H5_TOKEN_EXPIRE_MINUTES = 7 * 24 * 60L;

    // ============ 积分来源类型 ============
    public static final String SOURCE_SIGN_IN = "SIGN_IN";
    public static final String SOURCE_EXCHANGE = "EXCHANGE";
    public static final String SOURCE_REFUND = "REFUND";
    public static final String SOURCE_ADMIN_ADJUST = "ADMIN_ADJUST";
    public static final String SOURCE_CONTINUOUS_BONUS = "CONTINUOUS_BONUS";
    public static final String SOURCE_SIGN_REPAIR = "SIGN_REPAIR";

    // ============ 变动类型 ============
    /** 增加 */
    public static final String CHANGE_TYPE_INCREASE = "0";
    /** 扣减 */
    public static final String CHANGE_TYPE_DECREASE = "1";

    // ============ 订单状态 ============
    public static final String ORDER_STATUS_PENDING = "0"; // 待发货
    public static final String ORDER_STATUS_SHIPPED = "1"; // 已发货
    public static final String ORDER_STATUS_FINISHED = "2"; // 已完成
    public static final String ORDER_STATUS_CLOSED = "3"; // 已关闭

    // ============ 商品类型 ============
    public static final String GOODS_TYPE_REAL = "0";    // 实物
    public static final String GOODS_TYPE_VIRTUAL = "1"; // 虚拟
}
