package com.ldjt.emp.service.impl;

import com.ldjt.emp.entity.SysRoleMenu;
import com.ldjt.emp.mapper.SysRoleMenuMapper;
import com.ldjt.emp.service.SysRoleMenuService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU;

/**
 * 角色菜单关联服务实现类
 *
 * @author emp
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignMenusToRole(Long roleId, List<Long> menuIds) {
        // 1. 删除角色原有的菜单权限
        deleteByRoleId(roleId);
        
        // 2. 如果菜单列表为空，直接返回
        if (menuIds == null || menuIds.isEmpty()) {
            return true;
        }
        
        // 3. 批量插入新的菜单权限
        List<SysRoleMenu> roleMenus = new ArrayList<>();
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenus.add(roleMenu);
        }
        
        return saveBatch(roleMenus);
    }
    
    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(SYS_ROLE_MENU.MENU_ID)
                .where(SYS_ROLE_MENU.ROLE_ID.eq(roleId));
        
        List<SysRoleMenu> roleMenus = list(queryWrapper);
        return roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRoleId(Long roleId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_ROLE_MENU.ROLE_ID.eq(roleId));
        return remove(queryWrapper);
    }
}
