package com.ldjt.emp.common.entity;

/**
 * 租户实体接口
 * 所有需要租户隔离的实体都应实现此接口
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
public interface TenantEntity {
    
    /**
     * 获取租户ID
     * 
     * @return 租户ID
     */
    Long getTenantId();
    
    /**
     * 设置租户ID
     * 
     * @param tenantId 租户ID
     */
    void setTenantId(Long tenantId);
}
