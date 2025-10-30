package com.ldjt.emp.framework.tenant;

import com.ldjt.emp.common.exception.BusinessException;

/**
 * 租户上下文持有者
 * 使用ThreadLocal存储当前线程的租户ID
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
public class TenantContextHolder {
    
    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    
    /**
     * 设置租户ID
     * 
     * @param tenantId 租户ID
     */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }
    
    /**
     * 获取租户ID(如果未设置则返回null，不抛出异常)
     * 这是默认方法，用于向后兼容
     * 
     * @return 租户ID或null
     */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }
    
    /**
     * 获取租户ID(如果未设置则抛出异常)
     * 用于必须有租户的场景
     * 
     * @return 租户ID
     * @throws BusinessException 如果租户上下文未设置
     */
    public static Long getTenantIdOrThrow() {
        Long tenantId = TENANT_ID.get();
        if (tenantId == null) {
            throw new BusinessException("租户上下文未设置");
        }
        return tenantId;
    }
    
    /**
     * 清除租户ID
     */
    public static void clear() {
        TENANT_ID.remove();
    }
    
    /**
     * 检查是否已设置租户上下文
     * 
     * @return true-已设置, false-未设置
     */
    public static boolean isSet() {
        return TENANT_ID.get() != null;
    }
}
