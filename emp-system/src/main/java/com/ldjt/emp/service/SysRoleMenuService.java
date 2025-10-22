package com.ldjt.emp.service;

import com.ldjt.emp.entity.SysRoleMenu;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 角色菜单关联服务接口
 *
 * @author emp
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 为角色分配菜单权限
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    boolean assignMenusToRole(Long roleId, List<Long> menuIds);
    
    /**
     * 查询角色的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
    
    /**
     * 删除角色的所有菜单权限
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteByRoleId(Long roleId);
}
