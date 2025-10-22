package com.ldjt.emp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.entity.SysUser;
import com.ldjt.emp.entity.SysUserPost;
import com.ldjt.emp.entity.SysUserRole;
import com.ldjt.emp.framework.security.SecurityUtils;
import com.ldjt.emp.mapper.SysUserMapper;
import com.ldjt.emp.mapper.SysUserPostMapper;
import com.ldjt.emp.mapper.SysUserRoleMapper;
import com.ldjt.emp.service.SysUserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysUserTableDef.SYS_USER;
import static com.ldjt.emp.entity.table.SysUserPostTableDef.SYS_USER_POST;
import static com.ldjt.emp.entity.table.SysUserRoleTableDef.SYS_USER_ROLE;

/**
 * 用户服务实现类
 *
 * @author emp
 */
@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserPostMapper sysUserPostMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SysUser getUserByUsername(String username) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER.USERNAME.eq(username))
                .and(SYS_USER.DELETED.eq(0));
        return sysUserMapper.selectOneByQuery(queryWrapper);
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public void updateLoginInfo(Long userId, String loginIp) {
        sysUserMapper.updateLoginInfo(userId, loginIp, LocalDateTime.now());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        Set<String> permissions = new HashSet<>();

        // 超级管理员拥有所有权限
        SysUser user = getUserById(userId);
        if (user != null && "admin".equals(user.getUsername())) {
            permissions.add("*:*:*");
            return new ArrayList<>(permissions);
        }

        // 1. 查询用户的岗位ID列表
        List<Long> postIds = getUserPostIds(userId);

        // 2. 查询岗位关联的角色ID列表
        List<Long> postRoleIds = getPostRoleIds(postIds);

        // 3. 查询岗位角色的菜单权限
        if (!postRoleIds.isEmpty()) {
            List<String> postPermissions = getMenuPermissionsByRoleIds(postRoleIds);
            permissions.addAll(postPermissions);
        }

        // 4. 查询用户直接分配的角色ID列表
        List<Long> directRoleIds = getDirectUserRoleIds(userId);

        // 5. 查询直接角色的菜单权限
        if (!directRoleIds.isEmpty()) {
            List<String> directPermissions = getMenuPermissionsByRoleIds(directRoleIds);
            permissions.addAll(directPermissions);
        }

        // 6. 过滤掉空权限标识
        permissions.removeIf(perm -> perm == null || perm.trim().isEmpty());

        return new ArrayList<>(permissions);
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        Set<String> roles = new HashSet<>();

        // 超级管理员角色
        SysUser user = getUserById(userId);
        if (user != null && "admin".equals(user.getUsername())) {
            roles.add("admin");
            return new ArrayList<>(roles);
        }

        // 1. 查询用户的岗位ID列表
        List<Long> postIds = getUserPostIds(userId);

        // 2. 查询岗位关联的角色
        if (!postIds.isEmpty()) {
            List<String> postRoles = getRoleCodesByPostIds(postIds);
            roles.addAll(postRoles);
        }

        // 3. 查询用户直接分配的角色
        List<String> directRoles = getRoleCodesByUserId(userId);
        roles.addAll(directRoles);

        // 4. 过滤掉空角色标识
        roles.removeIf(role -> role == null || role.trim().isEmpty());

        return new ArrayList<>(roles);
    }
    /**
     * 查询岗位关联的角色ID列表
     */
    private List<Long> getPostRoleIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>();
        }
        return sysUserMapper.selectPostRoleIds(postIds);
    }

    /**
     * 查询用户直接分配的角色ID列表（内部方法）
     */
    private List<Long> getDirectUserRoleIds(Long userId) {
        return sysUserMapper.selectDirectUserRoleIds(userId);
    }

    /**
     * 根据角色ID列表查询菜单权限标识
     */
    private List<String> getMenuPermissionsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return sysUserMapper.selectMenuPermissionsByRoleIds(roleIds);
    }

    /**
     * 根据岗位ID列表查询角色标识
     */
    private List<String> getRoleCodesByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>();
        }
        return sysUserMapper.selectRoleCodesByPostIds(postIds);
    }

    /**
     * 根据用户ID查询直接分配的角色标识
     */
    private List<String> getRoleCodesByUserId(Long userId) {
        return sysUserMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public SysUser getUserById(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER.ID.eq(userId))
                .and(SYS_USER.DELETED.eq(0));
        return sysUserMapper.selectOneByQuery(queryWrapper);
    }

    @Override
    public boolean createUser(SysUser user) {
        // 检查用户名唯一性
        SysUser existing = getUserByUsername(user.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        // 手动设置审计字段（防止监听器未生效）
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            userId = 1L; // 系统用户ID
        }
        LocalDateTime now = LocalDateTime.now();
        user.setCreateBy(userId);
        user.setCreateTime(now);
        user.setUpdateBy(userId);
        user.setUpdateTime(now);

        return sysUserMapper.insert(user) > 0;
    }

    @Override
    public boolean updateUser(SysUser user) {
        return sysUserMapper.update(user) > 0;
    }

    @Override
    public boolean deleteUser(Long userId) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setDeleted(1);
        return sysUserMapper.update(user) > 0;
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);

        // 如果禁用用户，清除该用户的Token
        if (status == 0) {
            try {
                StpUtil.kickout(userId);
                log.info("用户[{}]被禁用，已清除Token", userId);
            } catch (Exception e) {
                log.warn("清除用户Token失败: {}", e.getMessage());
            }
        }

        return sysUserMapper.update(user) > 0;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        return sysUserMapper.update(user) > 0;
    }

    @Override
    public List<SysUser> listAllUsers() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER.DELETED.eq(0))
                .orderBy(SYS_USER.CREATE_TIME.desc());
        return sysUserMapper.selectListByQuery(queryWrapper);
    }

    @Override
    public Page<SysUser> pageQuery(com.ldjt.emp.dto.UserPageQueryDTO queryDTO) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER.DELETED.eq(0))
                // 只有当参数不为 null 且不为空字符串时才添加 like 查询条件
                .and(SYS_USER.USERNAME.like(queryDTO.getUsername(),
                     queryDTO.getUsername() != null && !queryDTO.getUsername().trim().isEmpty()))
                .and(SYS_USER.NICKNAME.like(queryDTO.getNickName(),
                     queryDTO.getNickName() != null && !queryDTO.getNickName().trim().isEmpty()))
                .and(SYS_USER.MOBILE.like(queryDTO.getPhoneNumber(),
                     queryDTO.getPhoneNumber() != null && !queryDTO.getPhoneNumber().trim().isEmpty()))
                .and(SYS_USER.STATUS.eq(queryDTO.getStatus(), queryDTO.getStatus() != null))
                // 只有当 deptId 不为 null 且不为 0 时才作为查询条件
                .and(SYS_USER.DEPT_ID.eq(queryDTO.getDeptId(),
                     queryDTO.getDeptId() != null && queryDTO.getDeptId() != 0))
                .orderBy(SYS_USER.CREATE_TIME.desc());

        com.mybatisflex.core.paginate.Page<SysUser> page = new com.mybatisflex.core.paginate.Page<>(
                queryDTO.getPageNum(),
                queryDTO.getPageSize()
        );

        return sysUserMapper.paginate(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 先删除用户原有的角色关联
        QueryWrapper deleteWrapper = QueryWrapper.create()
                .where(SYS_USER_ROLE.USER_ID.eq(userId));
        sysUserRoleMapper.deleteByQuery(deleteWrapper);

        // 批量插入新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }

        // 清除用户权限缓存
        try {
            StpUtil.getSessionByLoginId(userId).delete("permissions");
            StpUtil.getSessionByLoginId(userId).delete("roles");
            log.info("已清除用户[{}]的权限缓存", userId);
        } catch (Exception e) {
            log.warn("清除用户权限缓存失败: {}", e.getMessage());
        }

        return true;
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER_ROLE.USER_ID.eq(userId));
        List<SysUserRole> userRoles = sysUserRoleMapper.selectListByQuery(queryWrapper);
        return userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean batchDelete(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }

        for (Long userId : userIds) {
            deleteUser(userId);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPosts(Long userId, List<Long> postIds) {
        // 先删除用户原有的岗位关联
        QueryWrapper deleteWrapper = QueryWrapper.create()
                .where(SYS_USER_POST.USER_ID.eq(userId));
        sysUserPostMapper.deleteByQuery(deleteWrapper);

        // 批量插入新的岗位关联
        if (postIds != null && !postIds.isEmpty()) {
            for (Long postId : postIds) {
                SysUserPost userPost = new SysUserPost();
                userPost.setUserId(userId);
                userPost.setPostId(postId);
                sysUserPostMapper.insert(userPost);
            }
        }

        // 清除用户权限缓存（因为岗位关联角色，会影响权限）
        try {
            StpUtil.getSessionByLoginId(userId).delete("permissions");
            StpUtil.getSessionByLoginId(userId).delete("roles");
            log.info("已清除用户[{}]的权限缓存", userId);
        } catch (Exception e) {
            log.warn("清除用户权限缓存失败: {}", e.getMessage());
        }

        return true;
    }

    @Override
    public List<Long> getUserPostIds(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER_POST.USER_ID.eq(userId));
        List<SysUserPost> userPosts = sysUserPostMapper.selectListByQuery(queryWrapper);
        return userPosts.stream()
                .map(SysUserPost::getPostId)
                .collect(Collectors.toList());
    }
}
