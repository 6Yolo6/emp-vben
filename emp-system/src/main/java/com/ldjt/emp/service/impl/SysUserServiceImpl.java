package com.ldjt.emp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.dto.UserPageQueryDTO;
import com.ldjt.emp.entity.*;
import com.ldjt.emp.framework.tenant.TenantContextHolder;
import com.ldjt.emp.service.PermissionService;
import com.ldjt.emp.service.SysUserMenuService;
import com.ldjt.emp.service.SysUserTenantService;
import com.ldjt.emp.vo.user.UserPermissionVO;
import com.ldjt.emp.vo.user.UserTenantVO;
import com.ldjt.emp.vo.user.UserVO;
import com.ldjt.emp.converter.UserConverter;
import com.ldjt.emp.framework.security.SecurityUtils;
import com.ldjt.emp.mapper.*;
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
import static com.ldjt.emp.entity.table.SysUserTenantTableDef.SYS_USER_TENANT;

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

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysPostMapper sysPostMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserTenantService userTenantService;

    @Autowired
    private SysUserTenantMapper userTenantMapper;

    @Autowired
    private SysTenantMapper sysTenantMapper;

    @Autowired
    private SysTenantMapper tenantMapper;

    @Autowired
    private SysUserMenuService sysUserMenuService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysPostRoleMapper sysPostRoleMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserVO getUserVOById(Long id) {
        // 查询用户基本信息
        SysUser user = getUserById(id);
        if (user == null) {
            return null;
        }

        // 查询部门信息
        SysDept dept = null;
        if (user.getDeptId() != null) {
            dept = sysDeptMapper.selectOneById(user.getDeptId());
        }

        // 查询用户的岗位列表
        List<SysPost> posts = getPostsByUserId(id);

        // 查询用户的角色列表
        List<SysRole> roles = getRolesByUserId(id);

        // 转换为 VO
        UserVO vo = UserConverter.toVO(user, dept, posts, roles);

        // 查询租户配置
        List<UserTenantVO> tenants = userTenantService.getUserTenants(id);
        vo.setTenants(tenants);

        // 查询主岗位信息
        if (user.getMainPostId() != null) {
            SysPost mainPost = sysPostMapper.selectOneById(user.getMainPostId());
            if (mainPost != null) {
                vo.setMainPostName(mainPost.getPostName());
            }
        }

        return vo;
    }

    // 3. 实现 pageQueryVO 方法
    @Override
    public Page<UserVO> pageQueryVO(UserPageQueryDTO queryDTO) {
        // 获取当前租户ID
        Long currentTenantId = com.ldjt.emp.framework.tenant.TenantContextHolder.getTenantId();

        // 构建查询条件
        QueryWrapper userQuery = QueryWrapper.create()
                .where(SYS_USER.DELETED.eq(0))
                .and(SYS_USER.USERNAME.like(queryDTO.getUsername(),
                     queryDTO.getUsername() != null && !queryDTO.getUsername().trim().isEmpty()))
                .and(SYS_USER.NICKNAME.like(queryDTO.getNickName(),
                     queryDTO.getNickName() != null && !queryDTO.getNickName().trim().isEmpty()))
                .and(SYS_USER.MOBILE.like(queryDTO.getPhoneNumber(),
                     queryDTO.getPhoneNumber() != null && !queryDTO.getPhoneNumber().trim().isEmpty()))
                .and(SYS_USER.DEPT_ID.eq(queryDTO.getDeptId(),
                     queryDTO.getDeptId() != null && queryDTO.getDeptId() != 0))
                .orderBy(SYS_USER.CREATE_TIME.desc());

        // 应用数据权限过滤
        try {
            Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
            PermissionService permissionService =
                com.ldjt.emp.framework.context.ApplicationContextProvider.getBean(
                    PermissionService.class);
            permissionService.applyDataScope(userQuery, SYS_USER.DEPT_ID.getName(), userId);
        } catch (Exception e) {
            log.warn("应用数据权限失败: {}", e.getMessage());
        }

        // 分页查询用户
        Page<SysUser> userPage = sysUserMapper.paginate(
            new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()),
            userQuery
        );

        // 创建 VO 分页对象
        Page<UserVO> voPage = new Page<>(userPage.getPageNumber(), userPage.getPageSize());
        voPage.setTotalRow(userPage.getTotalRow());

        if (userPage.getRecords().isEmpty()) {
            voPage.setRecords(Collections.emptyList());
            return voPage;
        }

        // 收集所有需要查询的 ID
        List<Long> userIds = userPage.getRecords().stream()
                .map(SysUser::getId)
                .collect(Collectors.toList());

        // 1. 查询所有用户的租户配置（不应用租户过滤，需要查询所有租户配置以找到主租户）
        // 临时禁用租户过滤
        Long originalTenantId = TenantContextHolder.getTenantId();
        List<SysUserTenant> userTenants;
        try {
            TenantContextHolder.setTenantId(null); // 禁用租户过滤

            QueryWrapper tenantQuery = QueryWrapper.create()
                    .where(SYS_USER_TENANT.USER_ID.in(userIds));

            userTenants = userTenantMapper.selectListByQuery(tenantQuery);
        } finally {
            // 恢复租户上下文
            TenantContextHolder.setTenantId(originalTenantId);
        }

        // 按用户ID分组，并找出每个用户的主租户
        Map<Long, SysUserTenant> primaryTenantMap = new HashMap<>();

        // 第一遍：优先查找标记为主租户的配置
        for (SysUserTenant ut : userTenants) {
            Long userId = ut.getUserId();

            // 如果是主租户，直接使用（优先级最高）
            if (Boolean.TRUE.equals(ut.getIsPrimary())) {
                primaryTenantMap.put(userId, ut);
            }
        }



        // 2. 收集需要查询的租户ID、部门ID
        Set<Long> tenantIds = userTenants.stream()
                .map(SysUserTenant::getTenantId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> deptIds = userTenants.stream()
                .map(SysUserTenant::getDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 批量查询租户信息
        Map<Long, SysTenant> tenantMap = new HashMap<>();
        if (!tenantIds.isEmpty()) {
            List<SysTenant> tenants = sysTenantMapper.selectListByIds(tenantIds);
            if (tenants != null && !tenants.isEmpty()) {
                tenantMap = tenants.stream()
                        .collect(Collectors.toMap(SysTenant::getId, t -> t));
            }
        }

        // 4. 批量查询部门信息
        Map<Long, SysDept> deptMap = new HashMap<>();
        if (!deptIds.isEmpty()) {
            List<SysDept> depts = sysDeptMapper.selectListByIds(deptIds);
            if (depts != null && !depts.isEmpty()) {
                deptMap = depts.stream()
                        .collect(Collectors.toMap(SysDept::getId, dept -> dept));
            }
        }

        // 5. 批量查询用户在各租户下的岗位
        Map<String, List<SysPost>> userTenantPostsMap = getTenantPostsByUserIds(userIds);

        // 6. 收集所有岗位ID并批量查询岗位信息
        Set<Long> postIds = userTenants.stream()
                .map(SysUserTenant::getMainPostId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, SysPost> postMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<SysPost> posts = sysPostMapper.selectListByIds(postIds);
            if (posts != null && !posts.isEmpty()) {
                postMap = posts.stream()
                        .collect(Collectors.toMap(SysPost::getId, p -> p));
            }
        }

        // 7. 转换为 VO 列表，并应用状态筛选
        final Map<Long, SysTenant> finalTenantMap = tenantMap;
        final Map<Long, SysDept> finalDeptMap = deptMap;
        final Map<Long, SysPost> finalPostMap = postMap;
        final Integer statusFilter = queryDTO.getStatus();

        List<UserVO> voList = userPage.getRecords().stream()
                .map(user -> {
                    UserVO vo = UserConverter.toVO(user);

                    // 填充主单位信息
                    SysUserTenant primaryTenant = primaryTenantMap.get(user.getId());
                    if (primaryTenant != null) {
                        UserConverter.fillPrimaryTenantInfo(
                            vo,
                            primaryTenant,
                            finalTenantMap,
                            finalDeptMap,
                            finalPostMap,
                            userTenantPostsMap
                        );
                    } else {
                        log.warn("用户 {} 没有找到主租户配置", user.getId());
                    }

                    return vo;
                })
                // 应用状态筛选（基于主单位状态）
                .filter(vo -> statusFilter == null ||
                        (vo.getPrimaryStatus() != null && vo.getPrimaryStatus().equals(statusFilter)))
                .collect(Collectors.toList());

        // 更新总记录数（如果应用了状态筛选）
        if (statusFilter != null) {
            voPage.setTotalRow((long) voList.size());
        }

        // 设置记录列表
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 批量查询用户在各租户下的岗位
     * @param userIds 用户ID列表
     * @return Map<userId_tenantId, List<SysPost>>
     */
    private Map<String, List<SysPost>> getTenantPostsByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        // 查询用户岗位关联（包含租户ID）
        QueryWrapper userPostQuery = QueryWrapper.create()
                .where(SYS_USER_POST.USER_ID.in(userIds));
        List<SysUserPost> userPosts = sysUserPostMapper.selectListByQuery(userPostQuery);

        if (userPosts.isEmpty()) {
            return new HashMap<>();
        }

        // 收集所有岗位ID
        Set<Long> postIds = userPosts.stream()
                .map(SysUserPost::getPostId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 批量查询岗位
        Map<Long, SysPost> postMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<SysPost> posts = sysPostMapper.selectListByIds(postIds);
            postMap = posts.stream()
                    .collect(Collectors.toMap(SysPost::getId, p -> p));
        }

        // 按 userId_tenantId 分组
        final Map<Long, SysPost> finalPostMap = postMap;
        return userPosts.stream()
                .filter(up -> up.getTenantId() != null && up.getPostId() != null)
                .collect(Collectors.groupingBy(
                        up -> up.getUserId() + "_" + up.getTenantId(),
                        Collectors.mapping(
                                up -> finalPostMap.get(up.getPostId()),
                                Collectors.filtering(Objects::nonNull, Collectors.toList())
                        )
                ));
    }

    // 4. 辅助方法：根据用户ID查询岗位列表
    private List<SysPost> getPostsByUserId(Long userId) {
        QueryWrapper userPostQuery = QueryWrapper.create()
                .where(SYS_USER_POST.USER_ID.eq(userId));
        List<SysUserPost> userPosts = sysUserPostMapper.selectListByQuery(userPostQuery);

        if (userPosts.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> postIds = userPosts.stream()
                .map(SysUserPost::getPostId)
                .collect(Collectors.toList());

        return sysPostMapper.selectListByIds(postIds);
    }

    // 5. 辅助方法：根据用户ID查询角色列表
    private List<SysRole> getRolesByUserId(Long userId) {
        QueryWrapper userRoleQuery = QueryWrapper.create()
                .where(SYS_USER_ROLE.USER_ID.eq(userId));
        List<SysUserRole> userRoles = sysUserRoleMapper.selectListByQuery(userRoleQuery);

        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        return sysRoleMapper.selectListByIds(roleIds);
    }

    // 6. 辅助方法：批量查询用户的岗位
    private Map<Long, List<SysPost>> getPostsByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        // 查询用户岗位关联
        QueryWrapper userPostQuery = QueryWrapper.create()
                .where(SYS_USER_POST.USER_ID.in(userIds));
        List<SysUserPost> userPosts = sysUserPostMapper.selectListByQuery(userPostQuery);

        if (userPosts.isEmpty()) {
            return new HashMap<>();
        }

        // 查询岗位信息
        Set<Long> postIds = userPosts.stream()
                .map(SysUserPost::getPostId)
                .collect(Collectors.toSet());

        List<SysPost> posts = sysPostMapper.selectListByIds(postIds);
        Map<Long, SysPost> postMap = posts.stream()
                .collect(Collectors.toMap(SysPost::getId, post -> post));

        // 组装结果
        return userPosts.stream()
                .collect(Collectors.groupingBy(
                        SysUserPost::getUserId,
                        Collectors.mapping(
                                up -> postMap.get(up.getPostId()),
                                Collectors.filtering(Objects::nonNull, Collectors.toList())
                        )
                ));
    }

    // 7. 辅助方法：批量查询用户的角色
    private Map<Long, List<SysRole>> getRolesByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        // 查询用户角色关联
        QueryWrapper userRoleQuery = QueryWrapper.create()
                .where(SYS_USER_ROLE.USER_ID.in(userIds));
        List<SysUserRole> userRoles = sysUserRoleMapper.selectListByQuery(userRoleQuery);

        if (userRoles.isEmpty()) {
            return new HashMap<>();
        }

        // 查询角色信息
        Set<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toSet());

        List<SysRole> roles = sysRoleMapper.selectListByIds(roleIds);
        Map<Long, SysRole> roleMap = roles.stream()
                .collect(Collectors.toMap(SysRole::getId, role -> role));

        // 组装结果
        return userRoles.stream()
                .collect(Collectors.groupingBy(
                        SysUserRole::getUserId,
                        Collectors.mapping(
                                ur -> roleMap.get(ur.getRoleId()),
                                Collectors.filtering(Objects::nonNull, Collectors.toList())
                        )
                ));
    }

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

        // 获取当前租户ID
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            log.warn("当前租户ID为空，无法查询用户权限");
            return new ArrayList<>(permissions);
        }

        // 1. 查询用户在当前租户下的岗位ID列表
        List<Long> postIds = getUserPostIdsByTenant(userId, tenantId);

        // 2. 查询岗位关联的角色ID列表
        List<Long> postRoleIds = getPostRoleIdsByTenant(postIds, tenantId);

        // 3. 查询岗位角色的菜单权限
        if (!postRoleIds.isEmpty()) {
            List<String> postPermissions = getMenuPermissionsByRoleIds(postRoleIds, tenantId);
            permissions.addAll(postPermissions);
        }

        // 4. 查询用户在当前租户下直接分配的角色ID列表
        List<Long> directRoleIds = getDirectUserRoleIdsByTenant(userId, tenantId);

        // 5. 查询直接角色的菜单权限
        if (!directRoleIds.isEmpty()) {
            List<String> directPermissions = getMenuPermissionsByRoleIds(directRoleIds, tenantId);
            permissions.addAll(directPermissions);
        }

        // 6. 获取直接分配给用户的菜单权限
        List<Long> directMenuIds = sysUserMenuService.getUserDirectMenuIds(userId);
        if (directMenuIds != null && !directMenuIds.isEmpty()) {
            // 查询菜单权限标识
            List<SysMenu> menus = sysMenuMapper.selectListByIds(directMenuIds);
            for (SysMenu menu : menus) {
                if (menu.getPerms() != null && !menu.getPerms().trim().isEmpty()) {
                    permissions.add(menu.getPerms());
                }
            }
        }

        // 7. 过滤掉空权限标识
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

        // 获取当前租户ID
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            log.warn("当前租户ID为空，无法查询用户角色");
            return new ArrayList<>(roles);
        }

        // 1. 查询用户在当前租户下的岗位ID列表
        List<Long> postIds = getUserPostIdsByTenant(userId, tenantId);

        // 2. 查询岗位关联的角色
        if (!postIds.isEmpty()) {
            List<String> postRoles = getRoleCodesByPostIds(postIds, tenantId);
            roles.addAll(postRoles);
        }

        // 3. 查询用户在当前租户下直接分配的角色
        List<String> directRoles = getRoleCodesByUserId(userId, tenantId);
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
        // 注意：这个方法需要租户ID，但为了兼容性保留无租户参数的版本
        // 实际使用时应该调用带租户参数的版本
        Long tenantId = com.ldjt.emp.framework.tenant.TenantContextHolder.getTenantId();
        if (tenantId == null) {
            log.warn("租户ID为空，无法查询菜单权限");
            return new ArrayList<>();
        }
        return sysUserMapper.selectMenuPermissionsByRoleIds(roleIds, tenantId);
    }

    /**
     * 根据岗位ID列表查询角色标识
     */
    private List<String> getRoleCodesByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 注意：这个方法需要租户ID，但为了兼容性保留无租户参数的版本
        Long tenantId = com.ldjt.emp.framework.tenant.TenantContextHolder.getTenantId();
        if (tenantId == null) {
            log.warn("租户ID为空，无法查询角色标识");
            return new ArrayList<>();
        }
        return sysUserMapper.selectRoleCodesByPostIds(postIds, tenantId);
    }

    /**
     * 根据用户ID查询直接分配的角色标识
     */
    private List<String> getRoleCodesByUserId(Long userId) {
        // 注意：这个方法需要租户ID，但为了兼容性保留无租户参数的版本
        Long tenantId = com.ldjt.emp.framework.tenant.TenantContextHolder.getTenantId();
        if (tenantId == null) {
            log.warn("租户ID为空，无法查询角色标识");
            return new ArrayList<>();
        }
        return sysUserMapper.selectRoleCodesByUserId(userId, tenantId);
    }

    /**
     * 查询用户在指定租户下的岗位ID列表
     */
    private List<Long> getUserPostIdsByTenant(Long userId, Long tenantId) {
        return sysUserMapper.selectUserPostIdsByTenant(userId, tenantId);
    }

    /**
     * 查询岗位在指定租户下关联的角色ID列表
     */
    private List<Long> getPostRoleIdsByTenant(List<Long> postIds, Long tenantId) {
        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>();
        }
        return sysUserMapper.selectPostRoleIdsByTenant(postIds, tenantId);
    }

    /**
     * 查询用户在指定租户下直接分配的角色ID列表
     */
    private List<Long> getDirectUserRoleIdsByTenant(Long userId, Long tenantId) {
        return sysUserMapper.selectDirectUserRoleIdsByTenant(userId, tenantId);
    }

    /**
     * 根据角色ID列表和租户ID查询菜单权限标识
     */
    private List<String> getMenuPermissionsByRoleIds(List<Long> roleIds, Long tenantId) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return sysUserMapper.selectMenuPermissionsByRoleIds(roleIds, tenantId);
    }

    /**
     * 根据岗位ID列表和租户ID查询角色标识
     */
    private List<String> getRoleCodesByPostIds(List<Long> postIds, Long tenantId) {
        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>();
        }
        return sysUserMapper.selectRoleCodesByPostIds(postIds, tenantId);
    }

    /**
     * 根据用户ID和租户ID查询直接分配的角色标识
     */
    private List<String> getRoleCodesByUserId(Long userId, Long tenantId) {
        return sysUserMapper.selectRoleCodesByUserId(userId, tenantId);
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
        Long tenantId = com.ldjt.emp.framework.tenant.TenantContextHolder.getTenantId();

        // 先删除用户原有的角色关联（添加租户过滤）
        QueryWrapper deleteWrapper = QueryWrapper.create()
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .and(SYS_USER_ROLE.TENANT_ID.eq(tenantId));
        sysUserRoleMapper.deleteByQuery(deleteWrapper);

        // 批量插入新的角色关联（添加租户ID）
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setTenantId(tenantId); // 添加租户ID
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
        Long tenantId = com.ldjt.emp.framework.tenant.TenantContextHolder.getTenantId();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER_ROLE.USER_ID.eq(userId))
                .and(SYS_USER_ROLE.TENANT_ID.eq(tenantId));
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
        Long tenantId = com.ldjt.emp.framework.tenant.TenantContextHolder.getTenantId();

        // 先删除用户原有的岗位关联（添加租户过滤）
        QueryWrapper deleteWrapper = QueryWrapper.create()
                .where(SYS_USER_POST.USER_ID.eq(userId))
                .and(SYS_USER_POST.TENANT_ID.eq(tenantId));
        sysUserPostMapper.deleteByQuery(deleteWrapper);

        // 批量插入新的岗位关联（添加租户ID）
        if (postIds != null && !postIds.isEmpty()) {
            for (Long postId : postIds) {
                SysUserPost userPost = new SysUserPost();
                userPost.setUserId(userId);
                userPost.setPostId(postId);
                userPost.setTenantId(tenantId); // 添加租户ID
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
        Long tenantId = com.ldjt.emp.framework.tenant.TenantContextHolder.getTenantId();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER_POST.USER_ID.eq(userId))
                .and(SYS_USER_POST.TENANT_ID.eq(tenantId));
        List<SysUserPost> userPosts = sysUserPostMapper.selectListByQuery(queryWrapper);
        return userPosts.stream()
                .map(SysUserPost::getPostId)
                .collect(Collectors.toList());
    }


    // ==================== 多租户用户管理方法 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(com.ldjt.emp.dto.UserCreateDTO dto) {
        // 1. 检查用户名唯一性
        SysUser existing = getUserByUsername(dto.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 创建用户基本信息
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setSex(dto.getSex());
        user.setMainPostId(dto.getMainPostId());
        user.setRemark(dto.getRemark());
        user.setStatus(1);

        // 设置默认部门（主租户的部门或第一个租户的部门）
        if (dto.getTenants() != null && !dto.getTenants().isEmpty()) {
            com.ldjt.emp.dto.user.UserTenantDTO primaryTenant = dto.getTenants().stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsPrimary()))
                .findFirst()
                .orElse(dto.getTenants().get(0));
            user.setDeptId(primaryTenant.getDeptId());
        } else if (dto.getDeptId() != null) {
            // 兼容模式：使用旧的deptId字段
            user.setDeptId(dto.getDeptId());
        }

        // 设置审计字段
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            currentUserId = 1L;
        }
        LocalDateTime now = LocalDateTime.now();
        user.setCreateBy(currentUserId);
        user.setCreateTime(now);
        user.setUpdateBy(currentUserId);
        user.setUpdateTime(now);

        // 插入用户
        if (sysUserMapper.insert(user) <= 0) {
            throw new RuntimeException("创建用户失败");
        }

        // 3. 保存租户配置
        if (dto.getTenants() != null && !dto.getTenants().isEmpty()) {
            // 多租户模式
            userTenantService.saveUserTenants(user.getId(), dto.getTenants());
        } else {
            // 兼容模式：使用当前租户
            Long currentTenantId = com.ldjt.emp.framework.tenant.TenantContextHolder.getTenantId();
            if (currentTenantId != null) {
                com.ldjt.emp.dto.user.UserTenantDTO tenantDTO = new com.ldjt.emp.dto.user.UserTenantDTO();
                tenantDTO.setTenantId(currentTenantId);
                tenantDTO.setIsPrimary(true);
                tenantDTO.setDeptId(dto.getDeptId());
                tenantDTO.setRoleIds(dto.getRoleIds());
                tenantDTO.setPostIds(dto.getPostIds());
                tenantDTO.setStatus(dto.getStatus());
                userTenantService.saveUserTenants(user.getId(), java.util.Collections.singletonList(tenantDTO));
            }
        }

        // 4. 验证主岗位
        if (dto.getMainPostId() != null) {
            validateMainPost(user.getId(), dto.getMainPostId());
        }

        log.info("创建用户成功: userId={}, username={}", user.getId(), user.getUsername());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(Long userId, com.ldjt.emp.dto.UserUpdateDTO dto) {
        // 1. 检查用户是否存在
        SysUser existingUser = getUserById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 更新用户基本信息
        SysUser user = new SysUser();
        user.setId(userId);
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setMainPostId(dto.getMainPostId());
        user.setRemark(dto.getRemark());

        // 更新默认部门（主租户的部门或第一个租户的部门）
        if (dto.getTenants() != null && !dto.getTenants().isEmpty()) {
            com.ldjt.emp.dto.user.UserTenantDTO primaryTenant = dto.getTenants().stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsPrimary()))
                .findFirst()
                .orElse(dto.getTenants().get(0));
            user.setDeptId(primaryTenant.getDeptId());
        } else if (dto.getDeptId() != null) {
            // 兼容模式
            user.setDeptId(dto.getDeptId());
        }

        user.setUpdateBy(SecurityUtils.getUserId());
        user.setUpdateTime(LocalDateTime.now());

        if (sysUserMapper.update(user) <= 0) {
            throw new RuntimeException("更新用户失败");
        }

        // 3. 更新租户配置
        if (dto.getTenants() != null && !dto.getTenants().isEmpty()) {
            // 多租户模式
            userTenantService.saveUserTenants(userId, dto.getTenants());
        } else {
            // 兼容模式：使用当前租户
            Long currentTenantId = TenantContextHolder.getTenantId();
            if (currentTenantId != null && (dto.getRoleIds() != null || dto.getPostIds() != null)) {
                com.ldjt.emp.dto.user.UserTenantDTO tenantDTO = new com.ldjt.emp.dto.user.UserTenantDTO();
                tenantDTO.setTenantId(currentTenantId);
                tenantDTO.setIsPrimary(true);
                tenantDTO.setDeptId(dto.getDeptId());
                tenantDTO.setRoleIds(dto.getRoleIds());
                tenantDTO.setPostIds(dto.getPostIds());
                tenantDTO.setStatus(1);
                userTenantService.saveUserTenants(userId, java.util.Collections.singletonList(tenantDTO));
            }
        }

        // 4. 验证主岗位
        if (dto.getMainPostId() != null) {
            validateMainPost(userId, dto.getMainPostId());
        }

        // 5. 清除权限缓存
        try {
            StpUtil.getSessionByLoginId(userId).delete("permissions");
            StpUtil.getSessionByLoginId(userId).delete("roles");
            log.info("已清除用户权限缓存: userId={}", userId);
        } catch (Exception e) {
            log.warn("清除用户权限缓存失败: {}", e.getMessage());
        }

        log.info("更新用户成功: userId={}", userId);
        return true;
    }

    /**
     * 验证主岗位是否在用户的岗位列表中
     */
    private void validateMainPost(Long userId, Long mainPostId) {
        // 查询用户所有租户的所有岗位
        List<UserTenantVO> tenants = userTenantService.getUserTenants(userId);
        List<Long> allPostIds = tenants.stream()
            .filter(t -> t.getPostIds() != null)
            .flatMap(t -> t.getPostIds().stream())
            .collect(Collectors.toList());

        if (!allPostIds.contains(mainPostId)) {
            throw new RuntimeException("主岗位必须在用户的岗位列表中");
        }
    }

    @Override
    public UserPermissionVO getUserPermissionInfo(Long userId) {
        UserPermissionVO permissionVO = new UserPermissionVO();

        // 使用 Map 来去重，key 为 menuId
        Map<Long, UserPermissionVO.PermissionItem> permissionMap = new LinkedHashMap<>();

        // 1. 获取角色权限（优先级最低）
        List<Long> roleIds = getUserRoleIds(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            // 查询角色对应的菜单
            QueryWrapper roleMenuWrapper = QueryWrapper.create()
                    .select(com.ldjt.emp.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU.MENU_ID)
                    .from(com.ldjt.emp.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU)
                    .where(com.ldjt.emp.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU.ROLE_ID.in(roleIds));

            List<com.ldjt.emp.entity.SysRoleMenu> roleMenus = sysRoleMenuMapper.selectListByQuery(roleMenuWrapper);
            List<Long> menuIds = roleMenus.stream().map(com.ldjt.emp.entity.SysRoleMenu::getMenuId).collect(Collectors.toList());

            if (!menuIds.isEmpty()) {
                // 查询菜单详情
                List<com.ldjt.emp.entity.SysMenu> menus = sysMenuMapper.selectListByIds(menuIds);
                for (com.ldjt.emp.entity.SysMenu menu : menus) {
                    UserPermissionVO.PermissionItem item = new UserPermissionVO.PermissionItem();
                    item.setMenuId(menu.getId());
                    item.setParentId(menu.getParentId());
                    item.setMenuName(menu.getMenuName());
                    item.setMenuType(menu.getMenuType());
                    item.setPerms(menu.getPerms());
                    item.setPath(menu.getPath());
                    item.setOrderNum(menu.getOrderNum());
                    item.setSourceType("role");
                    item.setSourceName("角色");
                    permissionMap.put(menu.getId(), item);
                }
            }
        }

        // 2. 获取岗位权限（优先级中等）
        List<Long> postIds = getUserPostIds(userId);
        if (postIds != null && !postIds.isEmpty()) {
            // 查询岗位对应的角色
            QueryWrapper postRoleWrapper = QueryWrapper.create()
                    .select(com.ldjt.emp.entity.table.SysPostRoleTableDef.SYS_POST_ROLE.ROLE_ID)
                    .from(com.ldjt.emp.entity.table.SysPostRoleTableDef.SYS_POST_ROLE)
                    .where(com.ldjt.emp.entity.table.SysPostRoleTableDef.SYS_POST_ROLE.POST_ID.in(postIds));

            List<com.ldjt.emp.entity.SysPostRole> postRoles = sysPostRoleMapper.selectListByQuery(postRoleWrapper);
            List<Long> postRoleIds = postRoles.stream().map(com.ldjt.emp.entity.SysPostRole::getRoleId).collect(Collectors.toList());

            if (!postRoleIds.isEmpty()) {
                // 查询角色对应的菜单
                QueryWrapper roleMenuWrapper = QueryWrapper.create()
                        .select(com.ldjt.emp.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU.MENU_ID)
                        .from(com.ldjt.emp.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU)
                        .where(com.ldjt.emp.entity.table.SysRoleMenuTableDef.SYS_ROLE_MENU.ROLE_ID.in(postRoleIds));

                List<com.ldjt.emp.entity.SysRoleMenu> roleMenus = sysRoleMenuMapper.selectListByQuery(roleMenuWrapper);
                List<Long> menuIds = roleMenus.stream().map(com.ldjt.emp.entity.SysRoleMenu::getMenuId).collect(Collectors.toList());

                if (!menuIds.isEmpty()) {
                    // 查询菜单详情
                    List<com.ldjt.emp.entity.SysMenu> menus = sysMenuMapper.selectListByIds(menuIds);
                    for (com.ldjt.emp.entity.SysMenu menu : menus) {
                        UserPermissionVO.PermissionItem item = new UserPermissionVO.PermissionItem();
                        item.setMenuId(menu.getId());
                        item.setParentId(menu.getParentId());
                        item.setMenuName(menu.getMenuName());
                        item.setMenuType(menu.getMenuType());
                        item.setPerms(menu.getPerms());
                        item.setPath(menu.getPath());
                        item.setOrderNum(menu.getOrderNum());
                        item.setSourceType("post");
                        item.setSourceName("岗位");
                        permissionMap.put(menu.getId(), item); // 覆盖角色权限
                    }
                }
            }
        }

        // 3. 获取直接分配的权限（优先级最高）
        List<Long> directMenuIds = sysUserMenuService.getUserDirectMenuIds(userId);
        if (directMenuIds != null && !directMenuIds.isEmpty()) {
            // 查询菜单详情
            List<com.ldjt.emp.entity.SysMenu> menus = sysMenuMapper.selectListByIds(directMenuIds);
            for (com.ldjt.emp.entity.SysMenu menu : menus) {
                UserPermissionVO.PermissionItem item = new UserPermissionVO.PermissionItem();
                item.setMenuId(menu.getId());
                item.setParentId(menu.getParentId());
                item.setMenuName(menu.getMenuName());
                item.setMenuType(menu.getMenuType());
                item.setPerms(menu.getPerms());
                item.setPath(menu.getPath());
                item.setOrderNum(menu.getOrderNum());
                item.setSourceType("direct");
                item.setSourceName("直接分配");
                permissionMap.put(menu.getId(), item); // 覆盖岗位和角色权限
            }
        }

        // 转换为列表
        permissionVO.setPermissions(new ArrayList<>(permissionMap.values()));

        return permissionVO;
    }

    @Override
    public List<Long> getUserDirectMenuIds(Long userId) {
        return sysUserMenuService.getUserDirectMenuIds(userId);
    }

    @Override
    public boolean assignUserPermissions(Long userId, List<Long> menuIds) {
        return sysUserMenuService.assignUserMenus(userId, menuIds);
    }
}

