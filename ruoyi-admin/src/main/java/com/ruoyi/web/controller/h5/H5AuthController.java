package com.ruoyi.web.controller.h5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.points.domain.dto.LoginBodyDTO;
import com.ruoyi.points.service.IH5LoginService;
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

    /** 登录（首次自动注册） */
    @PostMapping("/login")
    public AjaxResult login(@Validated @RequestBody LoginBodyDTO body)
    {
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        String token;
        if ("password".equals(body.getLoginType())) {
            // 密码登录需要图形验证码校验
            sysLoginService.validateCaptcha(body.getPhone(), body.getCaptchaCode(), body.getUuid());
            token = h5LoginService.loginByPassword(body.getPhone(), body.getPassword(), ip);
        } else {
            // 短信登录使用短信验证码校验，无需图形验证码
            token = h5LoginService.loginByCode(body.getPhone(), body.getCode(), ip);
        }
        AjaxResult r = AjaxResult.success();
        r.put("token", token);
        return r;
    }

    /** 注册 */
    @PostMapping("/register")
    public AjaxResult register(@Validated @RequestBody LoginBodyDTO body)
    {
        // 图形验证码校验
        sysLoginService.validateCaptcha(body.getPhone(), body.getCaptchaCode(), body.getUuid());

        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        h5LoginService.register(body.getPhone(), body.getPassword(), ip);
        return AjaxResult.success("注册成功");
    }
}
