package com.ruoyi.points.service;

import com.ruoyi.points.domain.H5User;

/**
 * H5 端登录服务
 */
public interface IH5LoginService
{
    /** 密码登录（图形验证码在控制器层校验），返回 token */
    String loginByPassword(String phone, String password, String loginIp);

    /** 注册新用户（手机号 + 密码） */
    void register(String phone, String password, String loginIp);

    /** 解析 token 获取 H5 用户（用于过滤器/拦截器） */
    H5User getLoginUser(String token);

    /** 注销 */
    void logout(String token);
}
