package com.ldjt.emp.service.impl;

import com.ldjt.emp.dto.MenuTreeDTO;
import com.ldjt.emp.entity.SysMenu;
import com.ldjt.emp.framework.tenant.TenantContextHolder;
import com.ldjt.emp.mapper.SysMenuMapper;
import com.ldjt.emp.mapper.SysRoleMenuMapper;
import com.ldjt.emp.service.SysMenuService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.ldjt.emp.entity.table.SysMenuTableDef.SYS_MENU;
import static com.ldjt.emp.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static com.ldjt.emp.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;

/**
 * 菜单服务实现类
 *
 * @author emp
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    public SysMenuServiceImpl(SysMenuMapper menuMapper, SysRoleMenuMapper roleMenuMapper) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public List<MenuTreeDTO> getMenuTree() {
        // 查询所有菜单
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_MENU.DELETED.eq(0))
                .orderBy(SYS_MENU.ORDER_NUM.asc());
        List<SysMenu> allMenus = this.list(queryWrapper);
        
        // 构建树形结构，从根节点（parentId = 0）开始
        return buildMenuTree(allMenus, 0L);
    }
    
    @Override
    public List<MenuTreeDTO> getMenuTreeByUserId(Long userId) {
        Long tenantId = TenantContextHolder.getTenantId();
        
        // 查询用户拥有的菜单权限
        List<SysMenu> userMenus = menuMapper.selectListByQuery(
                QueryWrapper.create()
                        .select(SYS_MENU.ALL_COLUMNS)
                        .from(SYS_MENU)
                        .leftJoin(SYS_ROLE_MENU).on(SYS_MENU.ID.eq(SYS_ROLE_MENU.MENU_ID))
                        .leftJoin(SYS_USER_ROLE).on(SYS_ROLE_MENU.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID))
                        .where(SYS_USER_ROLE.USER_ID.eq(userId))
                        .and(SYS_MENU.TENANT_ID.eq(tenantId))
                        .and(SYS_MENU.STATUS.eq(1))
                        .and(SYS_MENU.DELETED.eq(0))
                        .orderBy(SYS_MENU.ORDER_NUM.asc())
        );
        
        // 转换为树形结构
        return buildMenuTree(userMenus, 0L);
    }
    
    @Override
    public List<MenuTreeDTO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<MenuTreeDTO> tree = new ArrayList<>();
        
        for (SysMenu menu : menus) {
            if (menu.getParentId() != null && menu.getParentId().equals(parentId)) {
                MenuTreeDTO node = new MenuTreeDTO();
                BeanUtils.copyProperties(menu, node);
                
                // 递归查找子菜单
                List<MenuTreeDTO> children = buildMenuTree(menus, menu.getId());
                node.setChildren(children);
                
                tree.add(node);
            }
        }
        
        return tree;
    }
    
    @Override
    public boolean createMenu(SysMenu menu) {
        // 验证菜单名称唯一性
        if (!isMenuNameUnique(menu.getMenuName(), menu.getParentId(), null)) {
            throw new RuntimeException("菜单名称已存在");
        }
        
        // 设置默认值
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getOrderNum() == null) {
            menu.setOrderNum(0);
        }
        
        return save(menu);
    }
    
    @Override
    public boolean updateMenu(SysMenu menu) {
        // 验证菜单名称唯一性
        if (!isMenuNameUnique(menu.getMenuName(), menu.getParentId(), menu.getId())) {
            throw new RuntimeException("菜单名称已存在");
        }
        
        // 不能将父菜单设置为自己
        if (menu.getId().equals(menu.getParentId())) {
            throw new RuntimeException("不能将父菜单设置为自己");
        }
        
        return updateById(menu);
    }
    
    @Override
    public boolean deleteMenu(Long menuId) {
        // 检查是否有子菜单
        if (hasChildren(menuId)) {
            throw new RuntimeException("该菜单下存在子菜单，无法删除");
        }
        
        // 检查是否有角色关联
        Long roleMenuCount = roleMenuMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(SYS_ROLE_MENU.MENU_ID.eq(menuId))
        );
        
        if (roleMenuCount > 0) {
            throw new RuntimeException("该菜单已分配给角色，无法删除");
        }
        
        return removeById(menuId);
    }
    
    @Override
    public boolean hasChildren(Long menuId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_MENU.PARENT_ID.eq(menuId))
                .and(SYS_MENU.DELETED.eq(0));
        return count(queryWrapper) > 0;
    }
    
    @Override
    public boolean isMenuNameUnique(String menuName, Long parentId, Long menuId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_MENU.MENU_NAME.eq(menuName))
                .and(SYS_MENU.PARENT_ID.eq(parentId))
                .and(SYS_MENU.DELETED.eq(0));
        
        // 如果是更新操作，排除当前菜单
        if (menuId != null) {
            queryWrapper.and(SYS_MENU.ID.ne(menuId));
        }
        
        return count(queryWrapper) == 0;
    }
}
