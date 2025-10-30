package com.ldjt.emp.service;

import com.ldjt.emp.dto.user.UserTenantDTO;
import com.ldjt.emp.entity.SysUserTenant;
import com.ldjt.emp.vo.user.UserTenantVO;

import java.util.List;

/**
 * 用户租户关联服务接口
 * 
 * @author EMP Team
 * @since 2025-10-27
 */
public interface SysUserTenantService {
    
    /**
     * 保存用户的租户配置
     * 
     * @param userId 用户ID
     * @param tenants 租户配置列表
     */
    void saveUserTenants(Long userId, List<UserTenantDTO> tenants);
    
    /**
     * 查询用户的所有租户配置
     * 
     * @param userId 用户ID
     * @return 租户配置列表
     */
    List<UserTenantVO> getUserTenants(Long userId);
    
    /**
     * 删除用户的所有租户配置
     * 
     * @param userId 用户ID
     */
    void deleteUserTenants(Long userId);
    
    /**
     * 查询用户在指定租户下的角色ID列表
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIdsByTenant(Long userId, Long tenantId);
    
    /**
     * 查询用户在指定租户下的岗位ID列表
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 岗位ID列表
     */
    List<Long> getUserPostIdsByTenant(Long userId, Long tenantId);
    
    /**
     * 查询用户在指定租户下的关联信息
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 用户租户关联信息
     */
    SysUserTenant getUserTenant(Long userId, Long tenantId);
    
    /**
     * 查询用户的所有租户关联（实体列表）
     * 
     * @param userId 用户ID
     * @return 租户关联实体列表
     */
    List<SysUserTenant> getUserTenantsByUserId(Long userId);
    
    /**
     * 设置主租户
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     */
    void setPrimaryTenant(Long userId, Long tenantId);
}
