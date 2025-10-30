package com.ldjt.emp.service;

import com.ldjt.emp.entity.SysTenant;
import com.mybatisflex.core.service.IService;

/**
 * 租户服务接口
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
public interface SysTenantService extends IService<SysTenant> {
    
    /**
     * 根据租户编码获取租户
     * 
     * @param tenantCode 租户编码
     * @return 租户信息
     */
    SysTenant getByCode(String tenantCode);
    
    /**
     * 检查租户编码是否唯一
     * 
     * @param tenantCode 租户编码
     * @param tenantId 租户ID(修改时传入,新增时传null)
     * @return true-唯一, false-不唯一
     */
    boolean checkTenantCodeUnique(String tenantCode, Long tenantId);
}
