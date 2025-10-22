package com.ldjt.emp.service;

import com.ldjt.emp.common.enums.DataScopeEnum;
import com.ldjt.emp.entity.SysRole;
import com.ldjt.emp.entity.SysUser;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;
import java.util.Set;

/**
 * 权限服务接口
 * 提供数据权限过滤功能
 * 
 * @author emp
 */
public interface PermissionService {
    
    /**
     * 获取用户的所有角色
     * 包括通过岗位获得的角色和直接分配的角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getUserRoles(Long userId);
    
    /**
     * 获取用户的数据权限范围
     * 如果用户有多个角色，取最大权限（ALL > DEPT_AND_CHILD > DEPT > CUSTOM > SELF）
     * 
     * @param userId 用户ID
     * @return 数据权限范围
     */
    DataScopeEnum getUserDataScope(Long userId);
    
    /**
     * 获取用户可以访问的部门ID集合
     * 根据用户的数据权限范围计算
     * 
     * @param userId 用户ID
     * @return 部门ID集合
     */
    Set<Long> getUserAccessibleDeptIds(Long userId);
    
    /**
     * 为查询添加数据权限过滤条件
     * 根据当前登录用户的数据权限自动添加部门过滤条件
     * 
     * @param queryWrapper 查询条件
     * @param deptIdColumn 部门ID字段名（如：SYS_USER.DEPT_ID）
     * @return 添加了数据权限过滤的查询条件
     */
    QueryWrapper applyDataScope(QueryWrapper queryWrapper, String deptIdColumn);
    
    /**
     * 为查询添加数据权限过滤条件（指定用户）
     * 
     * @param queryWrapper 查询条件
     * @param deptIdColumn 部门ID字段名
     * @param userId 用户ID
     * @return 添加了数据权限过滤的查询条件
     */
    QueryWrapper applyDataScope(QueryWrapper queryWrapper, String deptIdColumn, Long userId);
    
    /**
     * 检查用户是否有权限访问指定部门的数据
     * 
     * @param userId 用户ID
     * @param deptId 部门ID
     * @return true-有权限，false-无权限
     */
    boolean hasAccessToDept(Long userId, Long deptId);
    
    /**
     * 检查用户是否有权限访问指定用户的数据
     * 
     * @param currentUserId 当前用户ID
     * @param targetUser 目标用户
     * @return true-有权限，false-无权限
     */
    boolean hasAccessToUser(Long currentUserId, SysUser targetUser);
}
