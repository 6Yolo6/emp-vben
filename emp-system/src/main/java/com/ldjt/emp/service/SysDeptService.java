package com.ldjt.emp.service;

import com.ldjt.emp.dto.DeptTreeDTO;
import com.ldjt.emp.entity.SysDept;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author emp
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 查询部门树
     *
     * @return 部门树列表
     */
    List<DeptTreeDTO> getDeptTree();
    
    /**
     * 构建部门树
     *
     * @param depts 部门列表
     * @param parentId 父部门ID
     * @return 部门树
     */
    List<DeptTreeDTO> buildDeptTree(List<SysDept> depts, Long parentId);
    
    /**
     * 创建部门
     *
     * @param dept 部门信息
     * @return 是否成功
     */
    boolean createDept(SysDept dept);
    
    /**
     * 更新部门
     *
     * @param dept 部门信息
     * @return 是否成功
     */
    boolean updateDept(SysDept dept);
    
    /**
     * 删除部门
     *
     * @param deptId 部门ID
     * @return 是否成功
     */
    boolean deleteDept(Long deptId);
    
    /**
     * 检查是否有子部门
     *
     * @param deptId 部门ID
     * @return 是否有子部门
     */
    boolean hasChildren(Long deptId);
    
    /**
     * 检查部门下是否有用户
     *
     * @param deptId 部门ID
     * @return 是否有用户
     */
    boolean hasUsers(Long deptId);
    
    /**
     * 移动部门
     *
     * @param deptId 部门ID
     * @param newParentId 新的父部门ID
     * @return 是否成功
     */
    boolean moveDept(Long deptId, Long newParentId);
    
    /**
     * 根据租户ID查询部门树
     *
     * @param tenantId 租户ID
     * @return 部门树列表
     */
    List<DeptTreeDTO> getDeptTreeByTenant(Long tenantId);
}
