package com.ldjt.emp.service.impl;

import com.ldjt.emp.dto.user.UserTenantDTO;
import com.ldjt.emp.entity.*;
import com.ldjt.emp.mapper.*;
import com.ldjt.emp.service.SysUserTenantService;
import com.ldjt.emp.vo.user.UserTenantVO;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysUserTenantTableDef.SYS_USER_TENANT;
import static com.ldjt.emp.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static com.ldjt.emp.entity.table.SysUserPostTableDef.SYS_USER_POST;

/**
 * 用户租户关联服务实现
 *
 * @author EMP Team
 * @since 2025-10-27
 */
@Slf4j
@Service
public class SysUserTenantServiceImpl implements SysUserTenantService {

    @Autowired
    private SysUserTenantMapper userTenantMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private SysTenantMapper tenantMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserTenants(Long userId, List<UserTenantDTO> tenants) {
        if (tenants == null || tenants.isEmpty()) {
            log.warn("租户配置为空，用户ID: {}", userId);
            return;
        }

        // 1. 删除旧的租户关联
        deleteUserTenants(userId);

        // 2. 保存新的租户关联
        for (UserTenantDTO tenantDTO : tenants) {
            // 2.1 创建用户-租户关联
            SysUserTenant userTenant = new SysUserTenant();
            userTenant.setUserId(userId);
            userTenant.setTenantId(tenantDTO.getTenantId());
            userTenant.setDeptId(tenantDTO.getDeptId());
            userTenant.setIsPrimary(tenantDTO.getIsPrimary());
            userTenant.setStatus(tenantDTO.getStatus());
            userTenant.setMainPostId(tenantDTO.getMainPostId());
            userTenant.setJoinTime(new Date());
            userTenantMapper.insert(userTenant);

            // 2.2 保存该租户下的角色
            if (tenantDTO.getRoleIds() != null && !tenantDTO.getRoleIds().isEmpty()) {
                for (Long roleId : tenantDTO.getRoleIds()) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRole.setTenantId(tenantDTO.getTenantId());
                    userRoleMapper.insert(userRole);
                }
            }

            // 2.3 保存该租户下的岗位
            if (tenantDTO.getPostIds() != null && !tenantDTO.getPostIds().isEmpty()) {
                for (Long postId : tenantDTO.getPostIds()) {
                    SysUserPost userPost = new SysUserPost();
                    userPost.setUserId(userId);
                    userPost.setPostId(postId);
                    userPost.setTenantId(tenantDTO.getTenantId());
                    userPostMapper.insert(userPost);
                }
            }
        }

        log.info("保存用户租户配置成功，用户ID: {}, 租户数量: {}", userId, tenants.size());
    }

    @Override
    public List<UserTenantVO> getUserTenants(Long userId) {
        // 查询用户的所有租户关联
        QueryWrapper query = QueryWrapper.create()
            .where(SYS_USER_TENANT.USER_ID.eq(userId))
            .orderBy(SYS_USER_TENANT.IS_PRIMARY.desc()); // 主租户排在前面
        List<SysUserTenant> userTenants = userTenantMapper.selectListByQuery(query);

        if (userTenants.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserTenantVO> result = new ArrayList<>();
        for (SysUserTenant userTenant : userTenants) {
            UserTenantVO vo = new UserTenantVO();
            vo.setTenantId(userTenant.getTenantId());
            vo.setIsPrimary(userTenant.getIsPrimary());
            vo.setDeptId(userTenant.getDeptId());
            vo.setMainPostId(userTenant.getMainPostId());
            vo.setStatus(userTenant.getStatus());
            vo.setStatusName(getStatusName(userTenant.getStatus()));

            // 查询租户信息
            SysTenant tenant = tenantMapper.selectOneById(userTenant.getTenantId());
            if (tenant != null) {
                vo.setTenantName(tenant.getTenantName());
            }

            // 查询部门信息
            if (userTenant.getDeptId() != null) {
                SysDept dept = deptMapper.selectOneById(userTenant.getDeptId());
                if (dept != null) {
                    vo.setDeptName(dept.getDeptName());
                }
            }

            // 查询该租户下的角色
            List<Long> roleIds = getUserRoleIdsByTenant(userId, userTenant.getTenantId());
            vo.setRoleIds(roleIds);
            if (!roleIds.isEmpty()) {
                List<SysRole> roles = roleMapper.selectListByIds(roleIds);
                vo.setRoleNames(roles.stream()
                    .map(SysRole::getRoleName)
                    .collect(Collectors.toList()));
            }

            // 查询该租户下的岗位
            List<Long> postIds = getUserPostIdsByTenant(userId, userTenant.getTenantId());
            vo.setPostIds(postIds);
            if (!postIds.isEmpty()) {
                List<SysPost> posts = postMapper.selectListByIds(postIds);
                vo.setPostNames(posts.stream()
                    .map(SysPost::getPostName)
                    .collect(Collectors.toList()));

                // 填充主岗位名称
                if (userTenant.getMainPostId() != null) {
                    posts.stream()
                        .filter(p -> Objects.equals(p.getId(), userTenant.getMainPostId()))
                        .findFirst()
                        .ifPresent(p -> vo.setMainPostName(p.getPostName()));
                }
            }

            result.add(vo);
        }

        return result;
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "在职";
            case 2:
                return "辞职";
            case 3:
                return "调出";
            case 4:
                return "退休";
            default:
                return "未知";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserTenants(Long userId) {
        // 删除用户-租户关联
        QueryWrapper tenantQuery = QueryWrapper.create()
            .where(SYS_USER_TENANT.USER_ID.eq(userId));
        userTenantMapper.deleteByQuery(tenantQuery);

        // 删除用户-角色关联
        QueryWrapper roleQuery = QueryWrapper.create()
            .where(SYS_USER_ROLE.USER_ID.eq(userId));
        userRoleMapper.deleteByQuery(roleQuery);

        // 删除用户-岗位关联
        QueryWrapper postQuery = QueryWrapper.create()
            .where(SYS_USER_POST.USER_ID.eq(userId));
        userPostMapper.deleteByQuery(postQuery);

        log.info("删除用户租户配置成功，用户ID: {}", userId);
    }

    @Override
    public List<Long> getUserRoleIdsByTenant(Long userId, Long tenantId) {
        QueryWrapper query = QueryWrapper.create()
            .where(SYS_USER_ROLE.USER_ID.eq(userId))
            .and(SYS_USER_ROLE.TENANT_ID.eq(tenantId));
        List<SysUserRole> userRoles = userRoleMapper.selectListByQuery(query);
        return userRoles.stream()
            .map(SysUserRole::getRoleId)
            .collect(Collectors.toList());
    }

    @Override
    public List<Long> getUserPostIdsByTenant(Long userId, Long tenantId) {
        QueryWrapper query = QueryWrapper.create()
            .where(SYS_USER_POST.USER_ID.eq(userId))
            .and(SYS_USER_POST.TENANT_ID.eq(tenantId));
        List<SysUserPost> userPosts = userPostMapper.selectListByQuery(query);
        return userPosts.stream()
            .map(SysUserPost::getPostId)
            .collect(Collectors.toList());
    }

    @Override
    public SysUserTenant getUserTenant(Long userId, Long tenantId) {
        QueryWrapper query = QueryWrapper.create()
            .where(SYS_USER_TENANT.USER_ID.eq(userId))
            .and(SYS_USER_TENANT.TENANT_ID.eq(tenantId));
        return userTenantMapper.selectOneByQuery(query);
    }

    @Override
    public List<SysUserTenant> getUserTenantsByUserId(Long userId) {
        QueryWrapper query = QueryWrapper.create()
            .where(SYS_USER_TENANT.USER_ID.eq(userId))
            .orderBy(SYS_USER_TENANT.IS_PRIMARY.desc());
        return userTenantMapper.selectListByQuery(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPrimaryTenant(Long userId, Long tenantId) {
        // 1. 将所有租户设置为非主租户
        List<SysUserTenant> userTenants = getUserTenantsByUserId(userId);
        for (SysUserTenant userTenant : userTenants) {
            userTenant.setIsPrimary(false);
            userTenantMapper.update(userTenant);
        }

        // 2. 设置指定租户为主租户
        SysUserTenant primaryTenant = getUserTenant(userId, tenantId);
        if (primaryTenant == null) {
            throw new RuntimeException("用户不属于该租户");
        }
        primaryTenant.setIsPrimary(true);
        userTenantMapper.update(primaryTenant);

        log.info("设置主租户成功: userId={}, tenantId={}", userId, tenantId);
    }
}
