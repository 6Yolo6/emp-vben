package com.ldjt.emp.service.impl;

import com.ldjt.emp.entity.SysUserMenu;
import com.ldjt.emp.framework.tenant.TenantContextHolder;
import com.ldjt.emp.mapper.SysUserMenuMapper;
import com.ldjt.emp.service.SysUserMenuService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysUserMenuTableDef.SYS_USER_MENU;

/**
 * 用户菜单权限服务实现
 */
@Slf4j
@Service
public class SysUserMenuServiceImpl extends ServiceImpl<SysUserMenuMapper, SysUserMenu> implements SysUserMenuService {
    
    @Override
    public List<Long> getUserDirectMenuIds(Long userId) {
        Long tenantId = TenantContextHolder.getTenantId();
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER_MENU.USER_ID.eq(userId))
                .and(SYS_USER_MENU.TENANT_ID.eq(tenantId))
                .and(SYS_USER_MENU.DELETED.eq(0));
        
        List<SysUserMenu> userMenus = list(queryWrapper);
        return userMenus.stream()
                .map(SysUserMenu::getMenuId)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignUserMenus(Long userId, List<Long> menuIds) {
        Long tenantId = TenantContextHolder.getTenantId();
        
        // 1. 删除用户原有的直接权限
        QueryWrapper deleteWrapper = QueryWrapper.create()
                .where(SYS_USER_MENU.USER_ID.eq(userId))
                .and(SYS_USER_MENU.TENANT_ID.eq(tenantId));
        mapper.deleteByQuery(deleteWrapper);
        
        // 2. 批量插入新的权限关联
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysUserMenu userMenu = new SysUserMenu();
                userMenu.setUserId(userId);
                userMenu.setMenuId(menuId);
                userMenu.setTenantId(tenantId);
                save(userMenu);
            }
        }
        
        return true;
    }
}
