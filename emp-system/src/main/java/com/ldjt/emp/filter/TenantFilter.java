package com.ldjt.emp.filter;

import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.entity.SysTenant;
import com.ldjt.emp.framework.tenant.TenantContextHolder;
import com.ldjt.emp.service.SysTenantService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 租户识别过滤器
 * 从请求中识别租户并设置到上下文
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class TenantFilter implements Filter {
    
    private final SysTenantService tenantService;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        
        try {
            String tenantCode = resolveTenantCode(req);
            
            if (tenantCode != null) {
                SysTenant tenant = tenantService.getByCode(tenantCode);
                if (tenant == null) {
                    throw new BusinessException("租户不存在: " + tenantCode);
                }
                if (tenant.getStatus() != 1) {
                    throw new BusinessException("租户已停用: " + tenantCode);
                }
                
                TenantContextHolder.setTenantId(tenant.getId());
                log.debug("设置租户上下文: tenantId={}, tenantCode={}", 
                    tenant.getId(), tenantCode);
            }
            
            chain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }
    
    /**
     * 解析租户编码
     * 优先级: 请求头 > 子域名 > 路径
     */
    private String resolveTenantCode(HttpServletRequest request) {
        // 1. 从请求头获取
        String tenantCode = request.getHeader("X-Tenant-Code");
        if (tenantCode != null && !tenantCode.isEmpty()) {
            return tenantCode;
        }
        
        // 2. 从子域名获取
        String host = request.getServerName();
        tenantCode = extractFromSubdomain(host);
        if (tenantCode != null) {
            return tenantCode;
        }
        
        // 3. 从路径获取
        String path = request.getRequestURI();
        tenantCode = extractFromPath(path);
        
        return tenantCode;
    }
    
    /**
     * 从子域名提取租户编码
     * company1.example.com -> company1
     */
    private String extractFromSubdomain(String host) {
        if (host.contains(".")) {
            String[] parts = host.split("\\.");
            if (parts.length >= 3 && !"www".equals(parts[0])) {
                return parts[0];
            }
        }
        return null;
    }
    
    /**
     * 从路径提取租户编码
     * /tenant/company1/api/... -> company1
     */
    private String extractFromPath(String path) {
        if (path.startsWith("/tenant/")) {
            String[] parts = path.substring(8).split("/");
            if (parts.length > 0) {
                return parts[0];
            }
        }
        return null;
    }
}
