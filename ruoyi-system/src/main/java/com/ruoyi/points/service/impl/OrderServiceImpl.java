package com.ruoyi.points.service.impl;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.points.constant.PointsConstants;
import com.ruoyi.points.domain.Coupon;
import com.ruoyi.points.domain.UserCoupon;
import com.ruoyi.points.mapper.CouponMapper;
import com.ruoyi.points.mapper.UserCouponMapper;
import com.ruoyi.points.domain.Goods;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.domain.Order;
import com.ruoyi.points.domain.UserAddress;
import com.ruoyi.points.domain.dto.ExchangeDTO;
import com.ruoyi.points.mapper.GoodsMapper;
import com.ruoyi.points.mapper.H5UserMapper;
import com.ruoyi.points.mapper.OrderMapper;
import com.ruoyi.points.mapper.UserAddressMapper;
import com.ruoyi.points.service.IOrderService;
import com.ruoyi.points.service.IPointsDetailService;

/**
 * 订单/兑换 Service
 *
 * 并发控制要点：
 * 1) Redis 分布式锁 key: exchange:lock:{goodsId}:{userId}，防止同一用户重复点击导致重复下单。
 * 2) 库存扣减：使用 update ... where stock >= qty and version = ? （乐观锁）。
 *    更新影响行数 = 0 ⇒ 库存不足或版本过期，回滚。
 * 3) 积分扣减：update t_user set points_balance=points_balance-? where user_id=? and points_balance>=?
 *    影响行数 = 0 ⇒ 余额不足，抛出异常回滚整个事务（数据库会自动回滚库存）。
 */
@Service
public class OrderServiceImpl implements IOrderService
{
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired private OrderMapper orderMapper;
    @Autowired private GoodsMapper goodsMapper;
    @Autowired private H5UserMapper h5UserMapper;
    @Autowired private UserAddressMapper addressMapper;
    @Autowired private IPointsDetailService pointsDetailService;
    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private CouponMapper couponMapper;
    @Autowired private UserCouponMapper userCouponMapper;

    @Override public List<Order> selectOrderList(Order order) { return orderMapper.selectOrderList(order); }
    @Override public Order selectOrderById(Long orderId) { return orderMapper.selectOrderById(orderId); }
    @Override public List<Order> selectUserOrders(Long userId, String status) { return orderMapper.selectUserOrders(userId, status); }

    @Override
    public Order exchange(Long userId, ExchangeDTO dto)
    {
        if (dto.getQuantity() == null || dto.getQuantity() < 1) dto.setQuantity(1);

        // 1) 分布式锁（同一用户对同一商品 5 秒内禁止重复兑换）
        String lockKey = PointsConstants.H5_EXCHANGE_LOCK_PREFIX + dto.getGoodsId() + ":" + userId;
        String lockVal = IdUtils.fastSimpleUUID();
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(lockKey, lockVal, 5, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(ok))
        {
            throw new ServiceException("操作过于频繁，请稍后再试");
        }
        try
        {
            return doExchange(userId, dto);
        }
        finally
        {
            // 释放锁（仅删除自己持有的锁）
            String cur = redisTemplate.opsForValue().get(lockKey);
            if (lockVal.equals(cur)) redisTemplate.delete(lockKey);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    protected Order doExchange(Long userId, ExchangeDTO dto)
    {
        // 用户校验
        H5User user = h5UserMapper.selectUserById(userId);
        if (user == null || !"0".equals(user.getStatus()))
            throw new ServiceException("账号状态异常");

        // 商品校验
        Goods goods = goodsMapper.selectGoodsById(dto.getGoodsId());
        if (goods == null || !"1".equals(goods.getStatus()))
            throw new ServiceException("商品不存在或已下架");
        if (goods.getStock() < dto.getQuantity())
            throw new ServiceException("库存不足");

        // 限兑校验
        if (goods.getLimitPerUser() != null && goods.getLimitPerUser() > 0)
        {
            int already = goodsMapper.countUserExchangeQuantity(userId, goods.getGoodsId());
            if (already + dto.getQuantity() > goods.getLimitPerUser())
                throw new ServiceException("超出每人限兑数量(" + goods.getLimitPerUser() + ")");
        }

        boolean isReal = PointsConstants.GOODS_TYPE_REAL.equals(goods.getGoodsType());
        Integer totalPoints = 0;
        BigDecimal payAmount = BigDecimal.ZERO;

        // 校验优惠券
        UserCoupon uc = null;
        if (dto.getUserCouponId() != null) {
            uc = userCouponMapper.selectUserCouponById(dto.getUserCouponId());
            if (uc == null || !"0".equals(uc.getStatus()) || !uc.getUserId().equals(userId)) {
                throw new ServiceException("优惠券无效或不可用");
            }
            if (uc.getEndTime().before(new Date())) {
                throw new ServiceException("优惠券已过期");
            }
            Coupon coupon = uc.getCoupon();
            // 校验适用范围
            if (!"0".equals(coupon.getUseType())) {
                List<Long> refIds = couponMapper.selectCouponGoodsIds(coupon.getCouponId());
                if ("1".equals(coupon.getUseType()) && !refIds.contains(goods.getCategoryId())) {
                    throw new ServiceException("优惠券不适用于该分类");
                } else if ("2".equals(coupon.getUseType()) && !refIds.contains(goods.getGoodsId())) {
                    throw new ServiceException("优惠券不适用于该商品");
                }
            }
        }

        if (isReal)
        {
            // ========== 实物商品：使用金额，可使用优惠券，不扣积分 ==========
            BigDecimal unitPrice = goods.getPrice() != null ? goods.getPrice() : BigDecimal.ZERO;
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));

            // 优惠券抵扣（针对金额）
            BigDecimal couponDiscount = BigDecimal.ZERO;
            if (uc != null) {
                Coupon coupon = uc.getCoupon();
                if (coupon.getMinAmount() > 0 && totalPrice.compareTo(BigDecimal.valueOf(coupon.getMinAmount())) < 0) {
                    throw new ServiceException("未达到优惠券使用门槛");
                }
                if ("0".equals(coupon.getCouponType())) { // 满减
                    couponDiscount = BigDecimal.valueOf(coupon.getDiscountValue());
                } else if ("1".equals(coupon.getCouponType())) { // 折扣
                    couponDiscount = totalPrice.multiply(
                        BigDecimal.ONE.subtract(BigDecimal.valueOf(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100)))
                    ).setScale(2, RoundingMode.HALF_UP);
                } else if ("2".equals(coupon.getCouponType())) { // 无门槛
                    couponDiscount = BigDecimal.valueOf(coupon.getDiscountValue());
                }
            }

            payAmount = totalPrice.subtract(couponDiscount);
            if (payAmount.compareTo(BigDecimal.ZERO) < 0) payAmount = BigDecimal.ZERO;
            totalPoints = 0;
        }
        else
        {
            // ========== 虚拟商品：使用积分，不支持优惠券 ==========
            if (uc != null) {
                throw new ServiceException("虚拟商品不支持使用优惠券");
            }
            totalPoints = goods.getPoints() * dto.getQuantity();
            if (user.getPointsBalance() < totalPoints)
                throw new ServiceException("积分不足");
            payAmount = BigDecimal.ZERO;
        }

        // 实物商品：校验地址
        String consignee = null, phone = null, fullAddr = null;
        if (isReal)
        {
            if (dto.getAddressId() == null) throw new ServiceException("请选择收货地址");
            UserAddress addr = addressMapper.selectById(dto.getAddressId());
            if (addr == null || !addr.getUserId().equals(userId))
                throw new ServiceException("收货地址无效");
            consignee = addr.getConsignee();
            phone = addr.getPhone();
            fullAddr = StringUtils.format("{} {} {} {}",
                StringUtils.nvl(addr.getProvince(), ""),
                StringUtils.nvl(addr.getCity(), ""),
                StringUtils.nvl(addr.getDistrict(), ""),
                addr.getDetail());
        }

        // 1. 乐观锁扣库存
        int rows = goodsMapper.decreaseStock(goods.getGoodsId(), dto.getQuantity(), goods.getVersion());
        if (rows <= 0) throw new ServiceException("商品库存不足或并发冲突，请重试");

        // 2. 虚拟商品：扣积分；实物商品：不扣积分
        if (!isReal && totalPoints > 0) {
            rows = h5UserMapper.decreasePoints(userId, totalPoints);
            if (rows <= 0) throw new ServiceException("积分扣减失败（余额不足）");
        }

        // 3. 创建订单
        Order order = new Order();
        order.setOrderNo("PE" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random()*10000)));
        order.setUserId(userId);
        order.setUserPhone(user.getPhone());
        order.setGoodsId(goods.getGoodsId());
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCover(goods.getCoverImg());
        order.setGoodsType(goods.getGoodsType());
        order.setQuantity(dto.getQuantity());
        order.setPointsUsed(totalPoints);
        order.setPayAmount(payAmount);
        order.setStatus(PointsConstants.ORDER_STATUS_PENDING);
        order.setConsignee(consignee);
        order.setPhone(phone);
        order.setAddress(fullAddr);
        order.setRemark(dto.getRemark());
        order.setCreateBy(user.getNickname());
        orderMapper.insertOrder(order);

        // 4. 更新优惠券状态
        if (uc != null) {
            userCouponMapper.updateStatus(uc.getUserCouponId(), "1", order.getOrderId());
        }

        // 5. 虚拟商品自动发货完成
        if (PointsConstants.GOODS_TYPE_VIRTUAL.equals(goods.getGoodsType()))
        {
            Order finish = new Order();
            finish.setOrderId(order.getOrderId());
            finish.setStatus(PointsConstants.ORDER_STATUS_FINISHED);
            finish.setRemark("虚拟商品自动完成");
            orderMapper.updateOrder(finish);
            order.setStatus(PointsConstants.ORDER_STATUS_FINISHED);
            order.setFinishTime(new Date());
        }

        // 6. 写积分明细（仅虚拟商品扣了积分才记录）
        if (!isReal && totalPoints > 0) {
            Integer newBalance = user.getPointsBalance() - totalPoints;
            pointsDetailService.record(userId,
                PointsConstants.CHANGE_TYPE_DECREASE,
                PointsConstants.SOURCE_EXCHANGE,
                order.getOrderNo(), totalPoints, newBalance,
                "兑换商品：" + goods.getGoodsName(),
                user.getNickname());
        }

        log.info("用户[{}]兑换商品[{}]成功，订单[{}]，扣减积分[{}]，支付金额[{}]",
            userId, goods.getGoodsName(), order.getOrderNo(), totalPoints, payAmount);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int shipOrder(Order order, String operator)
    {
        Order exist = orderMapper.selectOrderById(order.getOrderId());
        if (exist == null) throw new ServiceException("订单不存在");
        if (!PointsConstants.ORDER_STATUS_PENDING.equals(exist.getStatus()))
            throw new ServiceException("订单状态不允许发货");
        if (PointsConstants.GOODS_TYPE_VIRTUAL.equals(exist.getGoodsType()))
            throw new ServiceException("虚拟商品无需发货");
        order.setUpdateBy(operator);
        return orderMapper.shipOrder(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int closeOrder(Long orderId, String reason, String operator)
    {
        Order exist = orderMapper.selectOrderById(orderId);
        if (exist == null) throw new ServiceException("订单不存在");
        if (PointsConstants.ORDER_STATUS_FINISHED.equals(exist.getStatus())
            || PointsConstants.ORDER_STATUS_CLOSED.equals(exist.getStatus()))
            throw new ServiceException("订单状态不允许关闭");

        int rows = orderMapper.closeOrder(orderId, reason);
        if (rows <= 0) throw new ServiceException("关闭订单失败");

        // 退还积分（仅虚拟商品实际扣过积分）
        if (exist.getPointsUsed() != null && exist.getPointsUsed() > 0) {
            h5UserMapper.increasePoints(exist.getUserId(), exist.getPointsUsed());
        }
        // 回滚库存
        goodsMapper.increaseStock(exist.getGoodsId(), exist.getQuantity());
        // 回退优惠券
        UserCoupon ucQuery = new UserCoupon();
        ucQuery.setOrderId(orderId);
        List<UserCoupon> ucs = userCouponMapper.selectUserCouponList(ucQuery);
        if (ucs != null && !ucs.isEmpty()) {
            UserCoupon uc = ucs.get(0);
            String status = uc.getEndTime().before(new Date()) ? "2" : "0"; // 如果已过原本有效期，则变为过期，否则恢复未使用
            userCouponMapper.updateStatus(uc.getUserCouponId(), status, null);
        }

        // 写积分明细（仅退还了积分才记录）
        if (exist.getPointsUsed() != null && exist.getPointsUsed() > 0) {
            H5User user = h5UserMapper.selectUserById(exist.getUserId());
            pointsDetailService.record(exist.getUserId(),
                PointsConstants.CHANGE_TYPE_INCREASE,
                PointsConstants.SOURCE_REFUND,
                exist.getOrderNo(), exist.getPointsUsed(),
                user.getPointsBalance(),
                "订单关闭退还：" + StringUtils.nvl(reason, ""),
                operator);
        }
        return rows;
    }

    @Override
    public int confirmReceipt(Long orderId, Long userId)
    {
        int rows = orderMapper.confirmReceipt(orderId, userId);
        if (rows <= 0) throw new ServiceException("订单状态异常");
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOrder(Order order, String operator)
    {
        order.setUpdateBy(operator);
        return orderMapper.updateOrder(order);
    }
}
