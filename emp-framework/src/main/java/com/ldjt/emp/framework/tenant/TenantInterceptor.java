package com.ldjt.emp.framework.tenant;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户拦截器
 * 自动从Session中恢复租户上下文
 * 
 * @author EMP Team
 * @since 2025-10-27
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        try {
            // 如果用户已登录，从Session中恢复租户上下文
            if (StpUtil.isLogin()) {
                Long tenantId = (Long) StpUtil.getSession().get("tenantId");
                if (tenantId != null) {
                    TenantContextHolder.setTenantId(tenantId);
                    log.debug("租户上下文已设置: tenantId={}", tenantId);
                }
            }
        } catch (Exception e) {
            log.warn("设置租户上下文失败: {}", e.getMessage());
        }
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, 
                               HttpServletResponse response, 
                               Object handler, 
                               Exception ex) {
        // 清除租户上下文，避免线程池复用导致的问题
        TenantContextHolder.clear();
    }
}
