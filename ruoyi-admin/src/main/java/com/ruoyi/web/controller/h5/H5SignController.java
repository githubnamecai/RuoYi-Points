package com.ruoyi.web.controller.h5;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.points.constant.PointsConstants;
import com.ruoyi.points.service.ISignService;

@RestController
@RequestMapping("/h5-api/sign")
public class H5SignController extends H5BaseController
{
    @Autowired private ISignService signService;
    @Autowired private StringRedisTemplate redisTemplate;

    /** 签到信息（含本月日历、连续天数、可补签日期） */
    @GetMapping("/info")
    public AjaxResult info()
    {
        return success(signService.getSignInfo(currentUserId()));
    }

    /** 签到 — 限流：同一用户每秒最多 1 次 */
    @PostMapping
    public AjaxResult doSign()
    {
        Long uid = currentUserId();
        String key = PointsConstants.H5_SIGN_RATE_LIMIT_PREFIX + uid;
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, "1", 1, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(ok)) throw new ServiceException("请求过于频繁");
        return success(signService.doSign(uid));
    }

    /** 补签：dateStr=yyyy-MM-dd */
    @PostMapping("/repair")
    public AjaxResult repair(@RequestParam("date") String date)
    {
        return success(signService.repair(currentUserId(), date));
    }
}
