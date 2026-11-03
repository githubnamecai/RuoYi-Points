package com.ruoyi.web.controller.h5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.points.constant.PointsConstants;
import com.ruoyi.points.domain.dto.LoginBodyDTO;
import com.ruoyi.points.service.IH5LoginService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.framework.web.service.SysLoginService;

/**
 * H5 端登录 / 注册（公开接口，无需 token）
 */
@RestController
@RequestMapping("/h5-api/auth")
public class H5AuthController extends BaseController
{
    @Autowired private IH5LoginService h5LoginService;
    @Autowired private SysLoginService sysLoginService;
    @Autowired private ISysConfigService configService;

    /**
     * H5 端开关配置（公开接口）
     *
     * 供登录页 / 注册页在未登录状态下判断「登录开关」与「注册开关」，
     * 用于隐藏注册入口、展示「系统关闭登录」占位页。
     */
    @GetMapping("/config")
    public AjaxResult config()
    {
        AjaxResult r = AjaxResult.success();
        r.put("loginEnabled", configService.selectH5LoginEnabled());
        r.put("registerEnabled", configService.selectH5RegisterEnabled());
        return r;
    }

    /** 登录（图形验证码 + 手机号密码） */
    @PostMapping("/login")
    public AjaxResult login(@Validated @RequestBody LoginBodyDTO body)
    {
        // 登录开关：仅当参数 h5.login.enabled 为 true 时允许登录
        if (!configService.selectH5LoginEnabled())
        {
            return AjaxResult.error(PointsConstants.H5_LOGIN_CLOSED_MSG);
        }

        // 图形验证码校验（一次性，校验后即失效）
        sysLoginService.validateCaptcha(body.getPhone(), body.getCaptchaCode(), body.getUuid());

        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        String token = h5LoginService.loginByPassword(body.getPhone(), body.getPassword(), ip);

        AjaxResult r = AjaxResult.success();
        r.put("token", token);
        return r;
    }

    /** 注册 */
    @PostMapping("/register")
    public AjaxResult register(@Validated @RequestBody LoginBodyDTO body)
    {
        // 登录已关闭时，注册账号也无意义，一并拦截
        if (!configService.selectH5LoginEnabled())
        {
            return AjaxResult.error(PointsConstants.H5_LOGIN_CLOSED_MSG);
        }
        // 注册开关：仅当参数 h5.register.enabled 为 true 时允许注册
        if (!configService.selectH5RegisterEnabled())
        {
            return AjaxResult.error(PointsConstants.H5_REGISTER_CLOSED_MSG);
        }

        // 图形验证码校验
        sysLoginService.validateCaptcha(body.getPhone(), body.getCaptchaCode(), body.getUuid());

        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        h5LoginService.register(body.getPhone(), body.getPassword(), ip);
        return AjaxResult.success("注册成功");
    }
}
