package com.ldjt.emp.gateway.filter;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关鉴权过滤器配置
 * 
 * @author emp
 */
@Configuration
public class AuthFilter {
    
    /**
     * 注册 Sa-Token 全局过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截所有路径
                .addInclude("/**")
                // 排除不需要鉴权的路径
                .addExclude("/auth/login", "/auth/logout", "/auth/captcha")
                .addExclude("/system/user/register")
                .addExclude("/druid/**")
                .addExclude("/actuator/**")
                .addExclude("/doc.html", "/webjars/**", "/v3/api-docs/**")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由，并排除/auth/login 用于开放登录
                    SaRouter.match("/**", r -> StpUtil.checkLogin());
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                });
    }
}
