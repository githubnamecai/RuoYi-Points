package com.ruoyi.web.controller.h5;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.points.domain.H5User;
import com.ruoyi.points.service.IH5LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * H5 端拦截器：除 /h5-api/auth/** 之外的接口都需要校验 H5 token
 */
@Component
public class H5TokenInterceptor implements HandlerInterceptor
{
    public static final String CTX_USER_KEY = "H5_LOGIN_USER";

    @Autowired
    private IH5LoginService h5LoginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws IOException
    {
        String path = request.getRequestURI();
        // 公开接口直接放行（登录/注册 + 商品列表/详情/分类）
        if (path.contains("/h5-api/auth/")
            || path.endsWith("/h5-api/goods/list")
            || path.endsWith("/h5-api/goods/categories")
            || path.matches(".*\\/h5-api\\/goods\\/\\d+$"))
        {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        if (token == null) token = request.getHeader("h5-token");

        H5User user = h5LoginService.getLoginUser(token);
        if (user == null)
        {
            writeJson(response, AjaxResult.error(401, "登录已失效，请重新登录"));
            return false;
        }
        if ("1".equals(user.getStatus()))
        {
            writeJson(response, AjaxResult.error(403, "账号已被冻结"));
            return false;
        }
        request.setAttribute(CTX_USER_KEY, user);
        return true;
    }

    private void writeJson(HttpServletResponse response, AjaxResult result) throws IOException
    {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
