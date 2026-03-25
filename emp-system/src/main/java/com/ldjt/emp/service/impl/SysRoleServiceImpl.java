package com.ldjt.emp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.entity.SysMenu;
import com.ldjt.emp.entity.SysRole;
import com.ldjt.emp.entity.SysRoleDept;
import com.ldjt.emp.entity.SysRoleMenu;
import com.ldjt.emp.entity.SysUser;
import com.ldjt.emp.framework.tenant.TenantContextHolder;
import com.ldjt.emp.mapper.SysMenuMapper;
import com.ldjt.emp.mapper.SysPostRoleMapper;
import com.ldjt.emp.mapper.SysRoleDeptMapper;
import com.ldjt.emp.mapper.SysRoleMapper;
import com.ldjt.emp.mapper.SysRoleMenuMapper;
import com.ldjt.emp.mapper.SysUserMapper;
import com.ldjt.emp.mapper.SysUserRoleMapper;
import com.ldjt.emp.service.SysRoleService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysMenuTableDef.SYS_MENU;
import static com.ldjt.emp.entity.table.SysPostRoleTableDef.SYS_POST_ROLE;
import static com.ldjt.emp.entity.table.SysRoleDeptTableDef.SYS_ROLE_DEPT;
import static com.ldjt.emp.entity.table.SysRoleTableDef.SYS_ROLE;
import static com.ldjt.emp.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static com.ldjt.emp.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static com.ldjt.emp.entity.table.SysUserTableDef.SYS_USER;

/**
 * 角色服务实现类
 *
 * @author emp
 */
@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysPostRoleMapper sysPostRoleMapper;

    @Autowired
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Override
    public Page<SysRole> page(Page<SysRole> page, String roleName) {
        Long tenantId = TenantContextHolder.getTenantId();
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_ROLE.TENANT_ID.eq(tenantId))
                .and(SYS_ROLE.DELETED.eq(0))
                .and(SYS_ROLE.ROLE_NAME.like(roleName, roleName != null))
                .orderBy(SYS_ROLE.ROLE_SORT.asc());

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignMenus(Long roleId, List<Long> menuIds) {
        Long tenantId = TenantContextHolder.getTenantId();

        // 先删除角色原有的菜单权限（添加租户过滤）
        QueryWrapper deleteWrapper = QueryWrapper.create()
                .where(SYS_ROLE_MENU.ROLE_ID.eq(roleId))
                .and(SYS_ROLE_MENU.TENANT_ID.eq(tenantId));
        sysRoleMenuMapper.deleteByQuery(deleteWrapper);

        // 批量插入新的菜单权限（添加租户ID）
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenu.setTenantId(tenantId); // 添加租户ID
                sysRoleMenuMapper.insert(roleMenu);
            }
        }

        // 清除相关用户的权限缓存
        clearUserPermissionCache(roleId);

        return true;
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        Long tenantId = TenantContextHolder.getTenantId();

        // 获取角色已分配的所有菜单ID（添加租户过滤）
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_ROLE_MENU.ROLE_ID.eq(roleId))
                .and(SYS_ROLE_MENU.TENANT_ID.eq(tenantId));
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectListByQuery(queryWrapper);
        List<Long> allMenuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());

        if (allMenuIds.isEmpty()) {
            return allMenuIds;
        }

        // 获取所有菜单，找出哪些是父节点
        QueryWrapper menuQueryWrapper = QueryWrapper.create()
                .where(SYS_MENU.DELETED.eq(0));
        List<SysMenu> allMenus = sysMenuMapper.selectListByQuery(menuQueryWrapper);

        // 收集所有父节点ID（有子节点的菜单ID）
        Set<Long> parentMenuIds = allMenus.stream()
                .filter(menu -> menu.getParentId() != null && menu.getParentId() != 0)
                .map(SysMenu::getParentId)
                .collect(Collectors.toSet());

        // 只返回叶子节点（不是父节点的菜单ID）
        // 这样Tree组件会自动根据叶子节点的选中状态来显示父节点的半选或全选状态
        return allMenuIds.stream()
                .filter(menuId -> !parentMenuIds.contains(menuId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignDepts(Long roleId, List<Long> deptIds) {
        Long tenantId = TenantContextHolder.getTenantId();

        // 先删除角色原有的自定义部门（添加租户过滤）
        QueryWrapper deleteWrapper = QueryWrapper.create()
                .where(SYS_ROLE_DEPT.ROLE_ID.eq(roleId))
                .and(SYS_ROLE_DEPT.TENANT_ID.eq(tenantId));
        sysRoleDeptMapper.deleteByQuery(deleteWrapper);

        // 批量插入新的自定义部门（添加租户ID）
        if (deptIds != null && !deptIds.isEmpty()) {
            for (Long deptId : deptIds) {
                SysRoleDept roleDept = new SysRoleDept();
                roleDept.setRoleId(roleId);
                roleDept.setDeptId(deptId);
                roleDept.setTenantId(tenantId); // 添加租户ID
                sysRoleDeptMapper.insert(roleDept);
            }
        }

        return true;
    }

    @Override
    public List<Long> getRoleDeptIds(Long roleId) {
        Long tenantId = TenantContextHolder.getTenantId();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_ROLE_DEPT.ROLE_ID.eq(roleId))
                .and(SYS_ROLE_DEPT.TENANT_ID.eq(tenantId));
        List<SysRoleDept> roleDepts = sysRoleDeptMapper.selectListByQuery(queryWrapper);
        return roleDepts.stream()
                .map(SysRoleDept::getDeptId)
                .collect(Collectors.toList());
    }

    @Override
    public List<SysRole> listByTenant() {
        Long tenantId = TenantContextHolder.getTenantId();
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_ROLE.TENANT_ID.eq(tenantId))
                .and(SYS_ROLE.DELETED.eq(0))
                .orderBy(SYS_ROLE.ROLE_SORT.asc());
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id) {
        // 检查是否有用户直接关联该角色
        QueryWrapper userQuery = QueryWrapper.create()
                .where(SYS_USER_ROLE.ROLE_ID.eq(id));
        long userCount = sysUserRoleMapper.selectCountByQuery(userQuery);

        if (userCount > 0) {
            throw new RuntimeException("该角色下还有" + userCount + "个用户，无法删除");
        }

        // 检查是否有岗位关联该角色
        QueryWrapper postQuery = QueryWrapper.create()
                .where(SYS_POST_ROLE.ROLE_ID.eq(id));
        long postCount = sysPostRoleMapper.selectCountByQuery(postQuery);

        if (postCount > 0) {
            throw new RuntimeException("该角色还关联了" + postCount + "个岗位，无法删除");
        }

        // 删除角色菜单关联
        QueryWrapper menuQuery = QueryWrapper.create()
                .where(SYS_ROLE_MENU.ROLE_ID.eq(id));
        sysRoleMenuMapper.deleteByQuery(menuQuery);

        // 删除角色自定义部门关联
        QueryWrapper deptQuery = QueryWrapper.create()
                .where(SYS_ROLE_DEPT.ROLE_ID.eq(id));
        sysRoleDeptMapper.deleteByQuery(deptQuery);

        // 执行删除
        return super.removeById(id);
    }

    /**
     * 清除角色相关用户的权限缓存
     */
    private void clearUserPermissionCache(Long roleId) {
        // 查询该角色下的所有用户
        List<SysUser> users = userMapper.selectListByQuery(
                QueryWrapper.create()
                        .select(SYS_USER.ID)
                        .from(SYS_USER)
                        .innerJoin(SYS_USER_ROLE).on(SYS_USER.ID.eq(SYS_USER_ROLE.USER_ID))
                        .where(SYS_USER_ROLE.ROLE_ID.eq(roleId))
                        .and(SYS_USER.DELETED.eq(0))
        );

        // 清除这些用户的权限缓存
        for (SysUser user : users) {
            StpUtil.kickout(user.getId());
            log.debug("清除用户权限缓存，用户ID：{}", user.getId());
        }
    }
}
