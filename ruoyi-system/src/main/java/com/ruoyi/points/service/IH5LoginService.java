package com.ruoyi.points.service;

import com.ruoyi.points.domain.H5User;

/**
 * H5 端登录/验证码服务
 */
public interface IH5LoginService
{
    /** 发送验证码（mock 输出到日志） */
    void sendSmsCode(String phone);

    /** 校验验证码 + 自动注册 + 颁发 token */
    String loginByCode(String phone, String code, String loginIp);
    String loginByPassword(String phone, String password, String loginIp);

    /** 注册新用户（手机号 + 密码） */
    void register(String phone, String password, String loginIp);

    /** 解析 token 获取 H5 用户（用于过滤器/拦截器） */
    H5User getLoginUser(String token);

    /** 注销 */
    void logout(String token);
}
