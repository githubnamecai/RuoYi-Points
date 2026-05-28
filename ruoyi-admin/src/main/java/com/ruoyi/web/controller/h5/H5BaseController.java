package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.points.domain.H5User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * H5 端控制器基类：提供 currentUser/currentUserId 工具方法
 */
public abstract class H5BaseController extends BaseController
{
    protected H5User currentUser()
    {
        HttpServletRequest req = ServletUtils.getRequest();
        Object u = req.getAttribute(H5TokenInterceptor.CTX_USER_KEY);
        if (u == null) throw new ServiceException("未登录", 401);
        return (H5User) u;
    }

    protected Long currentUserId() { return currentUser().getUserId(); }
}
