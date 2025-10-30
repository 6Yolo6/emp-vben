package com.ldjt.emp.service;

import com.ldjt.emp.entity.SysUserMenu;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户菜单权限服务接口
 */
public interface SysUserMenuService extends IService<SysUserMenu> {
    
    /**
     * 获取用户直接分配的菜单ID列表
     *
     * @param userId 用户ID
     * @return 菜单ID列表
     */
    List<Long> getUserDirectMenuIds(Long userId);
    
    /**
     * 分配用户菜单权限
     *
     * @param userId  用户ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    boolean assignUserMenus(Long userId, List<Long> menuIds);
}
