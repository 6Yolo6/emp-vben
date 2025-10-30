package com.ldjt.emp.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.service.PermissionService;
import com.ldjt.emp.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sa-Token权限认证接口实现
 * 支持多租户权限验证
 * 
 * @author emp
 */
@Component
@Slf4j
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private SysUserService sysUserService;
    
    @Autowired
    private PermissionService permissionService;

    /**
     * 返回一个账号所拥有的权限码集合
     * 基于当前租户获取用户权限
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        try {
            Long userId = Long.valueOf(loginId.toString());
            
            // 获取当前租户ID
            Long tenantId = getCurrentTenantId();
            
            if (tenantId != null) {
                // 多租户模式：基于当前租户获取权限
                log.debug("获取用户权限: userId={}, tenantId={}", userId, tenantId);
                return sysUserService.getUserPermissions(userId);
            } else {
                // 兼容单租户模式
                log.debug("获取用户权限(单租户模式): userId={}", userId);
                return sysUserService.getUserPermissions(userId);
            }
        } catch (Exception e) {
            log.error("获取用户权限失败，userId: {}", loginId, e);
            return List.of();
        }
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     * 基于当前租户获取用户角色
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            Long userId = Long.valueOf(loginId.toString());
            
            // 获取当前租户ID
            Long tenantId = getCurrentTenantId();
            
            if (tenantId != null) {
                // 多租户模式：基于当前租户获取角色
                log.debug("获取用户角色: userId={}, tenantId={}", userId, tenantId);
                return sysUserService.getUserRoles(userId);
            } else {
                // 兼容单租户模式
                log.debug("获取用户角色(单租户模式): userId={}", userId);
                return sysUserService.getUserRoles(userId);
            }
        } catch (Exception e) {
            log.error("获取用户角色失败，userId: {}", loginId, e);
            return List.of();
        }
    }
    
    /**
     * 获取当前租户ID
     * 从Sa-Token的Session中获取
     */
    private Long getCurrentTenantId() {
        try {
            if (StpUtil.isLogin()) {
                Object tenantId = StpUtil.getSession().get("tenantId");
                if (tenantId != null) {
                    return Long.valueOf(tenantId.toString());
                }
            }
        } catch (Exception e) {
            log.warn("获取当前租户ID失败", e);
        }
        return null;
    }
}
