package com.ldjt.emp.service;

import com.ldjt.emp.dto.MenuTreeDTO;
import com.ldjt.emp.entity.SysMenu;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author emp
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 查询菜单树
     *
     * @return 菜单树列表
     */
    List<MenuTreeDTO> getMenuTree();
    
    /**
     * 根据用户ID查询菜单树
     *
     * @param userId 用户ID
     * @return 用户有权限的菜单树
     */
    List<MenuTreeDTO> getMenuTreeByUserId(Long userId);
    
    /**
     * 构建菜单树
     *
     * @param menus 菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    List<MenuTreeDTO> buildMenuTree(List<SysMenu> menus, Long parentId);
    
    /**
     * 创建菜单
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    boolean createMenu(SysMenu menu);
    
    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    boolean updateMenu(SysMenu menu);
    
    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 是否成功
     */
    boolean deleteMenu(Long menuId);
    
    /**
     * 检查是否有子菜单
     *
     * @param menuId 菜单ID
     * @return 是否有子菜单
     */
    boolean hasChildren(Long menuId);
    
    /**
     * 检查菜单名称是否唯一
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @param menuId 菜单ID（更新时传入，新增时为null）
     * @return 是否唯一
     */
    boolean isMenuNameUnique(String menuName, Long parentId, Long menuId);
}
