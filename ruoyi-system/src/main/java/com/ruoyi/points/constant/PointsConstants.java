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

    /** H5 用户维度 token 索引 key 前缀（Set<uuid>），用于「改密后下线该用户全部会话」 */
    public static final String H5_USER_TOKENS_PREFIX = "h5:user:tokens:";

    /** H5 修改密码原密码错误次数 key 前缀（按用户维度计数） */
    public static final String H5_PWD_RETRY_PREFIX = "h5:pwd:retry:";

    /** H5 密码登录失败次数 key 前缀（按手机号维度计数） */
    public static final String H5_LOGIN_FAIL_PREFIX = "h5:login:fail:";

    /** H5 密码登录 IP 限流 key 前缀 */
    public static final String H5_LOGIN_IP_RATE_PREFIX = "h5:login:ip:";

    /** H5 兑换分布式锁 key 前缀 */
    public static final String H5_EXCHANGE_LOCK_PREFIX = "exchange:lock:";

    /** H5 签到限流 key 前缀 */
    public static final String H5_SIGN_RATE_LIMIT_PREFIX = "h5:sign:rate:";

    /** 商品库存 Redis key 前缀（预减库存） */
    public static final String GOODS_STOCK_PREFIX = "points:stock:";

    /** H5 token 有效期（分钟）默认值，实际取值见配置 h5.token-expire-minutes */
    public static final long H5_TOKEN_EXPIRE_MINUTES = 7 * 24 * 60L;

    // ============ 参数配置键（系统管理-参数设置） ============

    /** H5 登录开关：值为 true 时正常登录，其他值一律关闭登录 */
    public static final String CONFIG_H5_LOGIN_ENABLED = "h5.login.enabled";

    /** H5 注册开关：值为 true 时开放注册，其他值隐藏注册入口且禁止注册 */
    public static final String CONFIG_H5_REGISTER_ENABLED = "h5.register.enabled";

    /** H5 登录关闭时的提示语 */
    public static final String H5_LOGIN_CLOSED_MSG = "系统关闭登录";

    /** H5 注册关闭时的提示语 */
    public static final String H5_REGISTER_CLOSED_MSG = "系统未开启注册功能";

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
