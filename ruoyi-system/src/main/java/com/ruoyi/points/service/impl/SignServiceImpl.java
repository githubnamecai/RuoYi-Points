package com.ruoyi.points.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.points.constant.PointsConstants;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.domain.SignConfig;
import com.ruoyi.points.domain.SignRecord;
import com.ruoyi.points.domain.vo.SignInfoVO;
import com.ruoyi.points.mapper.H5UserMapper;
import com.ruoyi.points.mapper.SignConfigMapper;
import com.ruoyi.points.mapper.SignRecordMapper;
import com.ruoyi.points.service.IPointsDetailService;
import com.ruoyi.points.service.ISignService;

/**
 * 签到服务
 *
 * 连续天数计算规则：
 * - 若用户最近一次签到日期为「昨日」，则 continuous_days = lastContinuousDays + 1
 * - 若为「今日」，幂等返回当日记录
 * - 否则 continuous_days = 1（中断重置）
 *
 * 连续奖励：读取 t_sign_config.continuous_reward JSON, 形如 [{day:7,points:50}]
 *   达到对应 day 时额外发放 points（独立记录一条明细 source=CONTINUOUS_BONUS）。
 *
 * 补签：必须开启且最多 N 天内允许，扣除补签消耗积分；补签产生的记录不计入连续天数。
 */
@Service
public class SignServiceImpl implements ISignService
{
    private static final Logger log = LoggerFactory.getLogger(SignServiceImpl.class);
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired private SignConfigMapper configMapper;
    @Autowired private SignRecordMapper recordMapper;
    @Autowired private H5UserMapper h5UserMapper;
    @Autowired private IPointsDetailService pointsDetailService;

    @Override
    public SignConfig getConfig()
    {
        SignConfig c = configMapper.selectConfig();
        if (c == null)
        {
            c = new SignConfig();
            c.setEnabled("1");
            c.setBasePoints(10);
            c.setContinuousReward("[]");
            c.setRepairEnabled("0");
            c.setRepairCost(50);
            c.setRepairMaxDays(3);
        }
        return c;
    }

    @Override
    public int updateConfig(SignConfig config) { return configMapper.updateConfig(config); }

    @Override
    public SignInfoVO getSignInfo(Long userId)
    {
        SignConfig cfg = getConfig();
        H5User user = h5UserMapper.selectUserById(userId);
        if (user == null) throw new ServiceException("用户不存在");

        LocalDate today = LocalDate.now();
        Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());

        SignRecord todayRec = recordMapper.selectByUserAndDate(userId, todayDate);
        List<SignRecord> monthRecs = recordMapper.selectMonthRecords(
            userId, today.getYear(), today.getMonthValue());

        SignInfoVO vo = new SignInfoVO();
        vo.setSignedToday(todayRec != null);
        vo.setContinuousDays(computeCurrentContinuousDays(user, today));
        vo.setMonthlyDays(monthRecs.size());
        vo.setSignedDates(monthRecs.stream()
            .map(r -> DATE_FMT.format(r.getSignDate()))
            .collect(Collectors.toList()));

        // 补签可用日期：从今天往前数 repairMaxDays 天，过滤掉已签到与今天
        List<String> repairables = new ArrayList<>();
        if ("1".equals(cfg.getRepairEnabled()))
        {
            Set<String> signed = new HashSet<>(vo.getSignedDates());
            for (int i = 1; i <= cfg.getRepairMaxDays(); i++)
            {
                String d = DATE_FMT.format(java.sql.Date.valueOf(today.minusDays(i)));
                if (!signed.contains(d)) repairables.add(d);
            }
        }
        vo.setRepairableDates(repairables);

        SignInfoVO.SignConfigVO cv = new SignInfoVO.SignConfigVO();
        cv.setBasePoints(cfg.getBasePoints());
        cv.setContinuousReward(cfg.getContinuousReward());
        cv.setRepairEnabled(cfg.getRepairEnabled());
        cv.setRepairCost(cfg.getRepairCost());
        cv.setRepairMaxDays(cfg.getRepairMaxDays());
        vo.setConfig(cv);
        return vo;
    }

    /** 计算"当前"连续签到天数：若用户昨天/今天都没签到则为0 */
    private int computeCurrentContinuousDays(H5User user, LocalDate today)
    {
        if (user.getLastSignDate() == null) return 0;
        LocalDate last = user.getLastSignDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (last.isEqual(today) || last.isEqual(today.minusDays(1)))
            return user.getContinuousDays() == null ? 0 : user.getContinuousDays();
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SignRecord doSign(Long userId)
    {
        SignConfig cfg = getConfig();
        if (!"1".equals(cfg.getEnabled())) throw new ServiceException("签到功能已关闭");

        LocalDate today = LocalDate.now();
        Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // 幂等校验
        SignRecord exist = recordMapper.selectByUserAndDate(userId, todayDate);
        if (exist != null) throw new ServiceException("今日已签到");

        H5User user = h5UserMapper.selectUserById(userId);
        if (user == null) throw new ServiceException("用户不存在");
        if ("1".equals(user.getStatus())) throw new ServiceException("账号已冻结");

        // 计算连续天数
        int continuousDays = 1;
        if (user.getLastSignDate() != null)
        {
            LocalDate last = user.getLastSignDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (last.isEqual(today.minusDays(1)))
                continuousDays = (user.getContinuousDays() == null ? 0 : user.getContinuousDays()) + 1;
        }

        int basePoints = cfg.getBasePoints() == null ? 0 : cfg.getBasePoints();
        SignRecord record = new SignRecord();
        record.setUserId(userId);
        record.setSignDate(todayDate);
        record.setPoints(basePoints);
        record.setContinuousDays(continuousDays);
        record.setIsRepair("0");
        recordMapper.insert(record);

        // 增加积分 + 明细
        if (basePoints > 0)
        {
            h5UserMapper.increasePoints(userId, basePoints);
            Integer newBalance = user.getPointsBalance() + basePoints;
            pointsDetailService.record(userId, PointsConstants.CHANGE_TYPE_INCREASE,
                PointsConstants.SOURCE_SIGN_IN, record.getRecordId().toString(),
                basePoints, newBalance,
                "每日签到", user.getNickname());

            // 连续奖励
            Integer bonus = readContinuousBonus(cfg.getContinuousReward(), continuousDays);
            if (bonus != null && bonus > 0)
            {
                h5UserMapper.increasePoints(userId, bonus);
                pointsDetailService.record(userId, PointsConstants.CHANGE_TYPE_INCREASE,
                    PointsConstants.SOURCE_CONTINUOUS_BONUS, record.getRecordId().toString(),
                    bonus, newBalance + bonus,
                    "连续签到" + continuousDays + "天奖励", user.getNickname());
            }
        }

        // 更新用户签到信息
        h5UserMapper.updateSignInfo(userId, continuousDays, DATE_FMT.format(todayDate));
        log.info("用户[{}]签到成功，连续[{}]天，获得[{}]分", userId, continuousDays, basePoints);
        return record;
    }

    /**
     * 补签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SignRecord repair(Long userId, String dateStr)
    {
        SignConfig cfg = getConfig();
        if (!"1".equals(cfg.getRepairEnabled())) throw new ServiceException("补签功能未开启");
        if (StringUtils.isEmpty(dateStr)) throw new ServiceException("补签日期不能为空");

        LocalDate target;
        try { target = LocalDate.parse(dateStr); }
        catch (Exception e) { throw new ServiceException("日期格式错误"); }

        LocalDate today = LocalDate.now();
        if (!target.isBefore(today)) throw new ServiceException("仅可补签过去的日期");
        long diff = today.toEpochDay() - target.toEpochDay();
        if (diff > cfg.getRepairMaxDays()) throw new ServiceException("超出可补签范围");

        Date targetDate = Date.from(target.atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (recordMapper.selectByUserAndDate(userId, targetDate) != null)
            throw new ServiceException("该日已签到，无需补签");

        H5User user = h5UserMapper.selectUserById(userId);
        if (user == null) throw new ServiceException("用户不存在");

        int cost = cfg.getRepairCost() == null ? 0 : cfg.getRepairCost();
        if (cost > 0)
        {
            int rows = h5UserMapper.decreasePoints(userId, cost);
            if (rows <= 0) throw new ServiceException("积分不足，无法补签");
            pointsDetailService.record(userId, PointsConstants.CHANGE_TYPE_DECREASE,
                PointsConstants.SOURCE_SIGN_REPAIR, dateStr, cost,
                user.getPointsBalance() - cost,
                "补签消耗：" + dateStr, user.getNickname());
        }

        SignRecord record = new SignRecord();
        record.setUserId(userId);
        record.setSignDate(targetDate);
        record.setPoints(0);
        record.setContinuousDays(0);
        record.setIsRepair("1");
        recordMapper.insert(record);
        return record;
    }

    /** 读取连续签到奖励配置 */
    private Integer readContinuousBonus(String json, int days)
    {
        if (StringUtils.isEmpty(json)) return null;
        try
        {
            List<Map<String, Object>> arr = JSON.readValue(json, List.class);
            for (Map<String, Object> e : arr)
            {
                Integer day = (Integer) e.get("day");
                if (day != null && day.intValue() == days)
                {
                    Object p = e.get("points");
                    if (p instanceof Number) return ((Number) p).intValue();
                }
            }
        }
        catch (Exception e) { log.warn("连续签到奖励配置解析失败:{}", e.getMessage()); }
        return null;
    }
}
