package com.ruoyi.web.controller.h5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册 H5 拦截器
 */
@Configuration
public class H5WebMvcConfig implements WebMvcConfigurer
{
    @Autowired
    private H5TokenInterceptor h5TokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(h5TokenInterceptor)
            .addPathPatterns("/h5-api/**")
            .excludePathPatterns(
                "/h5-api/auth/**",
                "/h5-api/goods/list",
                "/h5-api/goods/categories",
                "/h5-api/goods/*"
            );
    }
}
