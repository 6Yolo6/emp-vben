package com.ldjt.emp.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.framework.tenant.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token配置类
 *
 * @author emp
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Autowired
    private TenantInterceptor tenantInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先注册租户拦截器（在Sa-Token拦截器之前）
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/**")
                .order(0);  // 设置为最高优先级
        
        // 然后注册Sa-Token拦截器
        // 注册Sa-Token拦截器
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定需要拦截的路径
            SaRouter.match("/**")
                // 排除接口
                .notMatch(
                    "/auth/login",           // 登录接口
                    "/auth/logout",          // 登出接口
                    "/auth/captcha",         // 验证码接口
                    "/auth/register",        // 注册接口
                    "/auth/tenants",         // 获取租户列表（登录前）
                    "/auth/user-tenants",    // 获取用户租户列表（登录前）
                    "/doc.html",             // Knife4j文档
                    "/swagger-ui/**",        // Swagger UI
                    "/swagger-resources/**", // Swagger资源
                    "/v3/api-docs/**",       // OpenAPI文档
                    "/webjars/**",           // Webjars资源
                    "/favicon.ico",          // 网站图标
                    "/error",                // 错误页面
                    "/actuator/**"           // Spring Boot Actuator
                )
                // 执行登录校验
                .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
