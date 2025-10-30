package com.ldjt.emp.framework.tenant;

import com.mybatisflex.core.tenant.TenantFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * MyBatis-Flex 租户插件处理器
 * 自动为SQL添加租户过滤条件
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Slf4j
@Component
public class TenantLineHandler implements TenantFactory {
    
    /**
     * 不需要租户隔离的表
     */
    private static final Set<String> IGNORE_TABLES = new HashSet<>(Arrays.asList(
        "sys_user",           // 用户表全局共享（通过sys_user_tenant关联）
        "sys_tenant",         // 租户表本身
        "sys_user_tenant",    // 用户租户关联表
        "sys_menu"            // 菜单表全局共享（可选）
    ));
    
    public Object[] getTenantIds() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            // 某些场景(如定时任务)可能没有租户上下文
            log.debug("租户上下文未设置,跳过租户过滤");
            return new Object[0];
        }
        return new Object[]{tenantId};
    }
    
    public String getTenantIdColumn() {
        return "tenant_id";
    }
    
    public boolean ignoreTable(String tableName) {
        boolean ignore = IGNORE_TABLES.contains(tableName.toLowerCase());
        if (ignore) {
            log.debug("表 {} 忽略租户过滤", tableName);
        }
        return ignore;
    }
}
