package com.ldjt.emp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.common.enums.DataScopeEnum;
import com.ldjt.emp.entity.SysDept;
import com.ldjt.emp.entity.SysRole;
import com.ldjt.emp.entity.SysRoleDept;
import com.ldjt.emp.entity.SysUser;
import com.ldjt.emp.mapper.SysDeptMapper;
import com.ldjt.emp.mapper.SysRoleDeptMapper;
import com.ldjt.emp.mapper.SysRoleMapper;
import com.ldjt.emp.mapper.SysUserMapper;
import com.ldjt.emp.service.PermissionService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysDeptTableDef.SYS_DEPT;
import static com.ldjt.emp.entity.table.SysPostRoleTableDef.SYS_POST_ROLE;
import static com.ldjt.emp.entity.table.SysRoleDeptTableDef.SYS_ROLE_DEPT;
import static com.ldjt.emp.entity.table.SysRoleTableDef.SYS_ROLE;
import static com.ldjt.emp.entity.table.SysUserPostTableDef.SYS_USER_POST;
import static com.ldjt.emp.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;

/**
 * 权限服务实现类
 * 
 * @author emp
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired
    private SysRoleMapper sysRoleMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private SysDeptMapper sysDeptMapper;
    
    @Autowired
    private SysRoleDeptMapper sysRoleDeptMapper;
    
    @Override
    public List<SysRole> getUserRoles(Long userId) {
        // 1. 获取用户直接分配的角色
        QueryWrapper directRoleQuery = QueryWrapper.create()
                .select(SYS_ROLE.ALL_COLUMNS)
                .from(SYS_ROLE)
                .innerJoin(SYS_USER_ROLE).on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ID))
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .and(SYS_ROLE.DELETED.eq(0))
                .and(SYS_ROLE.STATUS.eq(1));
        
        List<SysRole> directRoles = sysRoleMapper.selectListByQuery(directRoleQuery);
        
        // 2. 获取用户通过岗位获得的角色
        QueryWrapper postRoleQuery = QueryWrapper.create()
                .select(SYS_ROLE.ALL_COLUMNS)
                .from(SYS_ROLE)
                .innerJoin(SYS_POST_ROLE).on(SYS_POST_ROLE.ROLE_ID.eq(SYS_ROLE.ID))
                .innerJoin(SYS_USER_POST).on(SYS_USER_POST.POST_ID.eq(SYS_POST_ROLE.POST_ID))
                .where(SYS_USER_POST.USER_ID.eq(userId))
                .and(SYS_ROLE.DELETED.eq(0))
                .and(SYS_ROLE.STATUS.eq(1));
        
        List<SysRole> postRoles = sysRoleMapper.selectListByQuery(postRoleQuery);
        
        // 3. 合并去重
        Set<Long> roleIds = new HashSet<>();
        List<SysRole> allRoles = new ArrayList<>();
        
        for (SysRole role : directRoles) {
            if (roleIds.add(role.getId())) {
                allRoles.add(role);
            }
        }
        
        for (SysRole role : postRoles) {
            if (roleIds.add(role.getId())) {
                allRoles.add(role);
            }
        }
        
        return allRoles;
    }
    
    @Override
    public DataScopeEnum getUserDataScope(Long userId) {
        List<SysRole> roles = getUserRoles(userId);
        
        if (roles.isEmpty()) {
            return DataScopeEnum.SELF; // 没有角色，只能看自己的数据
        }
        
        // 取最大权限
        DataScopeEnum maxScope = DataScopeEnum.SELF;
        
        for (SysRole role : roles) {
            DataScopeEnum roleScope = DataScopeEnum.getByCode(role.getDataScope());
            
            // 如果有全部数据权限，直接返回
            if (roleScope == DataScopeEnum.ALL) {
                return DataScopeEnum.ALL;
            }
            
            // 权限优先级：ALL > DEPT_AND_CHILD > DEPT > CUSTOM > SELF
            if (roleScope.getCode() < maxScope.getCode()) {
                maxScope = roleScope;
            }
        }
        
        return maxScope;
    }
    
    @Override
    public Set<Long> getUserAccessibleDeptIds(Long userId) {
        DataScopeEnum dataScope = getUserDataScope(userId);
        SysUser user = sysUserMapper.selectOneById(userId);
        
        if (user == null || user.getDeptId() == null) {
            return Collections.emptySet();
        }
        
        Set<Long> deptIds = new HashSet<>();
        
        switch (dataScope) {
            case ALL:
                // 全部数据权限，返回null表示不限制
                return null;
                
            case DEPT:
                // 本部门数据权限
                deptIds.add(user.getDeptId());
                break;
                
            case DEPT_AND_CHILD:
                // 本部门及以下数据权限
                deptIds.add(user.getDeptId());
                deptIds.addAll(getChildDeptIds(user.getDeptId()));
                break;
                
            case CUSTOM:
                // 自定义数据权限
                deptIds.addAll(getCustomDeptIds(userId));
                break;
                
            case SELF:
                // 仅本人数据权限，返回空集合
                break;
        }
        
        return deptIds;
    }
    
    /**
     * 获取部门的所有子部门ID
     */
    private Set<Long> getChildDeptIds(Long deptId) {
        Set<Long> childIds = new HashSet<>();
        
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_DEPT.PARENT_ID.eq(deptId))
                .and(SYS_DEPT.DELETED.eq(0));
        
        List<SysDept> children = sysDeptMapper.selectListByQuery(query);
        
        for (SysDept child : children) {
            childIds.add(child.getId());
            // 递归获取子部门的子部门
            childIds.addAll(getChildDeptIds(child.getId()));
        }
        
        return childIds;
    }
    
    /**
     * 获取用户的自定义部门ID集合
     * 从用户的所有角色中获取自定义部门
     */
    private Set<Long> getCustomDeptIds(Long userId) {
        Set<Long> customDeptIds = new HashSet<>();
        
        // 获取用户的所有角色
        List<SysRole> roles = getUserRoles(userId);
        
        // 遍历角色,获取自定义部门
        for (SysRole role : roles) {
            if (role.getDataScope() != null && role.getDataScope() == 5) {
                // 查询该角色的自定义部门
                QueryWrapper query = QueryWrapper.create()
                        .where(SYS_ROLE_DEPT.ROLE_ID.eq(role.getId()));
                List<SysRoleDept> roleDepts = sysRoleDeptMapper.selectListByQuery(query);
                
                for (SysRoleDept roleDept : roleDepts) {
                    customDeptIds.add(roleDept.getDeptId());
                }
            }
        }
        
        return customDeptIds;
    }
    
    @Override
    public QueryWrapper applyDataScope(QueryWrapper queryWrapper, String deptIdColumn) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        return applyDataScope(queryWrapper, deptIdColumn, userId);
    }
    
    @Override
    public QueryWrapper applyDataScope(QueryWrapper queryWrapper, String deptIdColumn, Long userId) {
        DataScopeEnum dataScope = getUserDataScope(userId);
        SysUser user = sysUserMapper.selectOneById(userId);
        
        if (user == null) {
            // 用户不存在，不返回任何数据
            return queryWrapper.where("1 = 0");
        }
        
        switch (dataScope) {
            case ALL:
                // 全部数据权限，不添加过滤条件
                break;
                
            case DEPT:
                // 本部门数据权限
                if (user.getDeptId() != null) {
                    queryWrapper.where(deptIdColumn + " = " + user.getDeptId());
                }
                break;
                
            case DEPT_AND_CHILD:
                // 本部门及以下数据权限
                Set<Long> deptIds = getUserAccessibleDeptIds(userId);
                if (deptIds != null && !deptIds.isEmpty()) {
                    queryWrapper.where(deptIdColumn + " IN (" + 
                            deptIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")");
                }
                break;
                
            case CUSTOM:
                // 自定义数据权限
                Set<Long> customDeptIds = getCustomDeptIds(userId);
                if (customDeptIds != null && !customDeptIds.isEmpty()) {
                    queryWrapper.where(deptIdColumn + " IN (" + 
                            customDeptIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")");
                } else {
                    // 如果没有配置自定义部门,默认只能看本部门
                    if (user.getDeptId() != null) {
                        queryWrapper.where(deptIdColumn + " = " + user.getDeptId());
                    }
                }
                break;
                
            case SELF:
                // 仅本人数据权限
                queryWrapper.where("create_by = " + userId);
                break;
        }
        
        return queryWrapper;
    }
    
    @Override
    public boolean hasAccessToDept(Long userId, Long deptId) {
        if (deptId == null) {
            return true;
        }
        
        Set<Long> accessibleDeptIds = getUserAccessibleDeptIds(userId);
        
        // null 表示全部数据权限
        if (accessibleDeptIds == null) {
            return true;
        }
        
        return accessibleDeptIds.contains(deptId);
    }
    
    @Override
    public boolean hasAccessToUser(Long currentUserId, SysUser targetUser) {
        if (targetUser == null) {
            return false;
        }
        
        DataScopeEnum dataScope = getUserDataScope(currentUserId);
        
        switch (dataScope) {
            case ALL:
                return true;
                
            case SELF:
                return currentUserId.equals(targetUser.getId());
                
            default:
                return hasAccessToDept(currentUserId, targetUser.getDeptId());
        }
    }
}
