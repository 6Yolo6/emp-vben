package com.ldjt.emp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.LoginRequest;
import com.ldjt.emp.dto.LoginResponse;
import com.ldjt.emp.entity.SysTenant;
import com.ldjt.emp.entity.SysUser;
import com.ldjt.emp.entity.SysUserTenant;
import com.ldjt.emp.service.AuthService;
import com.ldjt.emp.service.SysTenantService;
import com.ldjt.emp.service.SysUserService;
import com.ldjt.emp.service.SysUserTenantService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 认证服务实现类
 *
 * @author emp
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysTenantService tenantService;

    @Autowired
    private SysUserTenantService userTenantService;

    @Override
    public LoginResponse login(LoginRequest loginRequest, String loginIp) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String tenantCode = loginRequest.getTenantCode();

        // 如果未指定租户，使用默认租户
        if (!StringUtils.hasText(tenantCode)) {
            tenantCode = "default";
        }

        log.info("用户登录: {}, 租户: {}, IP: {}", username, tenantCode, loginIp);

        // 1. 验证租户
        SysTenant tenant = tenantService.getByCode(tenantCode);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        if (tenant.getStatus() != 1) {
            throw new BusinessException("租户已停用");
        }
        if (tenant.getExpireTime() != null && tenant.getExpireTime().before(new Date())) {
            throw new BusinessException("租户已过期");
        }

        // 2. 查询用户
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 4. 验证密码
        if (!sysUserService.checkPassword(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 5. 验证用户是否属于该租户
        SysUserTenant userTenant = userTenantService.getUserTenant(user.getId(), tenant.getId());
        if (userTenant == null) {
            throw new BusinessException("用户该单位无账号");
        }
        if (userTenant.getStatus() != 1) {
            throw new BusinessException("用户在该单位已停用");
        }

        // 6. 登录成功，生成Token
        StpUtil.login(user.getId());

        // 7. 在Session中保存租户信息
        StpUtil.getSession().set("tenantId", tenant.getId());
        StpUtil.getSession().set("tenantCode", tenant.getTenantCode());
        StpUtil.getSession().set("tenantName", tenant.getTenantName());

        String token = StpUtil.getTokenValue();
        long expires = StpUtil.getTokenTimeout();

        // 8. 更新登录信息
        sysUserService.updateLoginInfo(user.getId(), loginIp);

        // 9. 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpires(expires);
        response.setUser(buildUserInfo(user, tenant, loginIp));

        log.info("用户登录成功: {}, 租户: {}, IP: {}", username, tenantCode, loginIp);
        return response;
    }

    @Override
    public boolean logout() {
        try {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                log.info("用户登出: {}", loginId);
                StpUtil.logout();
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("用户登出失败", e);
            throw new BusinessException("登出失败");
        }
    }

    @Override
    public LoginResponse.UserInfo getCurrentUserInfo() {
        // 检查登录状态
        StpUtil.checkLogin();

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserService.getUserById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取当前租户信息
        Long tenantId = (Long) StpUtil.getSession().get("tenantId");
        SysTenant tenant = tenantService.getById(tenantId);

        if (tenant == null) {
            throw new BusinessException("租户信息不存在");
        }

        return buildUserInfo(user, tenant, user.getLoginIp());
    }

    @Override
    public LoginResponse.TokenInfo refreshToken() {
        // 检查登录状态
        StpUtil.checkLogin();

        // 刷新Token
        StpUtil.renewTimeout(StpUtil.getTokenTimeout());
        String newToken = StpUtil.getTokenValue();
        long expires = StpUtil.getTokenTimeout();

        LoginResponse.TokenInfo tokenInfo = new LoginResponse.TokenInfo();
        tokenInfo.setToken(newToken);
        tokenInfo.setExpires(expires);

        log.info("Token刷新成功, userId: {}", StpUtil.getLoginIdAsLong());
        return tokenInfo;
    }

    @Override
    public LoginResponse.UserInfo switchTenant(String tenantCode) {
        // 检查登录状态
        StpUtil.checkLogin();

        Long userId = StpUtil.getLoginIdAsLong();

        log.info("用户切换租户: userId={}, tenantCode={}", userId, tenantCode);

        // 1. 验证目标租户
        SysTenant tenant = tenantService.getByCode(tenantCode);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        if (tenant.getStatus() != 1) {
            throw new BusinessException("租户已停用");
        }

        // 2. 验证用户是否属于目标租户
        SysUserTenant userTenant = userTenantService.getUserTenant(userId, tenant.getId());
        if (userTenant == null) {
            throw new BusinessException("您不属于该单位");
        }
        if (userTenant.getStatus() != 1) {
            throw new BusinessException("您在该单位已停用");
        }

        // 3. 切换租户上下文
        StpUtil.getSession().set("tenantId", tenant.getId());
        StpUtil.getSession().set("tenantCode", tenant.getTenantCode());
        StpUtil.getSession().set("tenantName", tenant.getTenantName());

        // 4. 获取用户信息
        SysUser user = sysUserService.getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        log.info("用户切换租户成功: userId={}, tenantCode={}", userId, tenantCode);
        return buildUserInfo(user, tenant, user.getLoginIp());
    }
    
    @Override
    public LoginResponse.UserInfo switchTenantById(Long tenantId) {
        // 检查登录状态
        StpUtil.checkLogin();

        Long userId = StpUtil.getLoginIdAsLong();

        log.info("用户切换租户: userId={}, tenantId={}", userId, tenantId);

        // 1. 验证目标租户
        SysTenant tenant = tenantService.getById(tenantId);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        if (tenant.getStatus() != 1) {
            throw new BusinessException("租户已停用");
        }

        // 2. 验证用户是否属于目标租户
        SysUserTenant userTenant = userTenantService.getUserTenant(userId, tenantId);
        if (userTenant == null) {
            throw new BusinessException("您不属于该单位");
        }
        if (userTenant.getStatus() != 1) {
            throw new BusinessException("您在该单位已停用");
        }

        // 3. 切换租户上下文
        StpUtil.getSession().set("tenantId", tenant.getId());
        StpUtil.getSession().set("tenantCode", tenant.getTenantCode());
        StpUtil.getSession().set("tenantName", tenant.getTenantName());

        // 4. 清除权限缓存
        StpUtil.getSession().delete("permissions");
        StpUtil.getSession().delete("roles");

        // 5. 获取用户信息
        SysUser user = sysUserService.getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        log.info("用户切换租户成功: userId={}, tenantId={}, tenantName={}", userId, tenantId, tenant.getTenantName());
        return buildUserInfo(user, tenant, user.getLoginIp());
    }

    @Override
    public List<com.ldjt.emp.vo.tenant.TenantVO> getUserAvailableTenants(String username) {
        // 1. 查询用户
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 查询用户关联的所有租户
        List<SysUserTenant> userTenants = userTenantService.getUserTenantsByUserId(user.getId());
        if (userTenants.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 3. 获取租户ID列表
        List<Long> tenantIds = userTenants.stream()
                .filter(ut -> ut.getStatus() == 1) // 只返回启用的关联
                .map(SysUserTenant::getTenantId)
                .collect(java.util.stream.Collectors.toList());

        if (tenantIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 4. 查询租户详情
        List<SysTenant> tenants = tenantService.listByIds(tenantIds);

        // 5. 过滤有效的租户（状态正常且未过期）
        Date now = new Date();
        return tenants.stream()
                .filter(t -> t.getStatus() == 1) // 状态正常
                .filter(t -> t.getExpireTime() == null || t.getExpireTime().after(now)) // 未过期
                .map(this::convertToTenantVO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<com.ldjt.emp.vo.tenant.TenantVO> getAllAvailableTenants() {
        // 查询所有租户
        QueryWrapper query = QueryWrapper.create()
                .where(com.ldjt.emp.entity.table.SysTenantTableDef.SYS_TENANT.DELETED.eq(0))
                .and(com.ldjt.emp.entity.table.SysTenantTableDef.SYS_TENANT.STATUS.eq(1))
                .orderBy(com.ldjt.emp.entity.table.SysTenantTableDef.SYS_TENANT.CREATE_TIME.asc());

        List<SysTenant> tenants = tenantService.list(query);

        // 过滤未过期的租户
        Date now = new Date();
        return tenants.stream()
                .filter(t -> t.getExpireTime() == null || t.getExpireTime().after(now))
                .map(this::convertToTenantVO)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 转换为租户VO
     */
    private com.ldjt.emp.vo.tenant.TenantVO convertToTenantVO(SysTenant tenant) {
        com.ldjt.emp.vo.tenant.TenantVO vo = new com.ldjt.emp.vo.tenant.TenantVO();
        vo.setId(tenant.getId());
        vo.setTenantCode(tenant.getTenantCode());
        vo.setTenantName(tenant.getTenantName());
        vo.setTenantType(tenant.getTenantType());
        vo.setContactName(tenant.getContactName());
        vo.setContactPhone(tenant.getContactPhone());
        vo.setContactEmail(tenant.getContactEmail());
        vo.setDomain(tenant.getDomain());
        vo.setLogo(tenant.getLogo());
        vo.setExpireTime(tenant.getExpireTime());
        vo.setAccountLimit(tenant.getAccountLimit());
        vo.setStorageLimit(tenant.getStorageLimit());
        vo.setStatus(tenant.getStatus());
        vo.setRemark(tenant.getRemark());
        vo.setCreateTime(tenant.getCreateTime());
        vo.setUpdateTime(tenant.getUpdateTime());
        return vo;
    }

    /**
     * 构建用户信息
     */
    private LoginResponse.UserInfo buildUserInfo(SysUser user, SysTenant tenant, String loginIp) {
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getMobile());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setDeptId(user.getDeptId());
        userInfo.setStatus(user.getStatus() == 1 ? "正常" : "停用");
        userInfo.setRemark(user.getRemark());
        userInfo.setLastLoginIp(loginIp);

        // 设置租户信息
        userInfo.setTenantId(tenant.getId());
        userInfo.setTenantCode(tenant.getTenantCode());
        userInfo.setTenantName(tenant.getTenantName());

        // 获取用户角色和权限(基于当前租户)
        List<String> roles = sysUserService.getUserRoles(user.getId());
        List<String> permissions = sysUserService.getUserPermissions(user.getId());
        userInfo.setRoles(roles);
        userInfo.setPermissions(permissions);

        return userInfo;
    }
    
    @Override
    public LoginResponse switchToUser(Long targetUserId, Long tenantId) {
        // 1. 检查当前用户是否登录
        StpUtil.checkLogin();
        Long currentUserId = StpUtil.getLoginIdAsLong();
        
        // 2. 检查当前用户是否是管理员
        SysUser currentUser = sysUserService.getUserById(currentUserId);
        if (currentUser == null || !"admin".equals(currentUser.getUsername())) {
            throw new BusinessException("只有管理员才能切换到其他用户");
        }
        
        // 3. 检查目标用户是否存在
        SysUser targetUser = sysUserService.getUserById(targetUserId);
        if (targetUser == null) {
            throw new BusinessException("目标用户不存在");
        }
        if (targetUser.getStatus() != 1) {
            throw new BusinessException("目标用户已停用");
        }
        
        // 4. 获取目标用户的租户信息
        List<com.ldjt.emp.vo.user.UserTenantVO> tenants = 
            userTenantService.getUserTenants(targetUserId);
        
        if (tenants == null || tenants.isEmpty()) {
            throw new BusinessException("目标用户没有关联任何单位");
        }
        
        // 5. 确定要登录的租户
        SysTenant tenant = null;
        if (tenantId != null) {
            // 指定了租户ID，验证用户是否属于该租户
            boolean belongsToTenant = tenants.stream()
                .anyMatch(t -> t.getTenantId().equals(tenantId));
            if (!belongsToTenant) {
                throw new BusinessException("目标用户不属于指定的单位");
            }
            tenant = tenantService.getById(tenantId);
        } else {
            // 未指定租户，使用主租户
            com.ldjt.emp.vo.user.UserTenantVO primaryTenant = tenants.stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsPrimary()))
                .findFirst()
                .orElse(tenants.get(0));
            tenant = tenantService.getById(primaryTenant.getTenantId());
        }
        
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        if (tenant.getStatus() != 1) {
            throw new BusinessException("租户已停用");
        }
        
        // 6. 保存原始用户ID到session（用于切换回来）
        StpUtil.getSession().set("originalUserId", currentUserId);
        StpUtil.getSession().set("originalUsername", currentUser.getUsername());
        
        // 7. 切换到目标用户
        StpUtil.logout(); // 先登出当前用户
        StpUtil.login(targetUserId); // 登录为目标用户
        
        // 8. 设置租户上下文
        StpUtil.getSession().set("tenantId", tenant.getId());
        StpUtil.getSession().set("tenantCode", tenant.getTenantCode());
        StpUtil.getSession().set("tenantName", tenant.getTenantName());
        
        // 9. 获取新的 token
        String newToken = StpUtil.getTokenValue();
        long expires = StpUtil.getTokenTimeout();
        
        // 10. 记录日志
        log.warn("管理员切换用户: admin={}, targetUser={}, targetUserId={}, tenantId={}, tenantName={}", 
            currentUser.getUsername(), targetUser.getUsername(), targetUserId, tenant.getId(), tenant.getTenantName());
        
        // 11. 构建响应（包含新token）
        LoginResponse response = new LoginResponse();
        response.setToken(newToken);
        response.setExpires(expires);
        response.setUser(buildUserInfo(targetUser, tenant, targetUser.getLoginIp()));
        
        return response;
    }
    
    @Override
    public LoginResponse switchBackToOriginal() {
        // 1. 检查是否登录
        StpUtil.checkLogin();
        
        // 2. 获取原始用户ID
        Object originalUserIdObj = StpUtil.getSession().get("originalUserId");
        if (originalUserIdObj == null) {
            throw new BusinessException("没有可切换回的原始账号");
        }
        
        Long originalUserId = Long.valueOf(originalUserIdObj.toString());
        String originalUsername = (String) StpUtil.getSession().get("originalUsername");
        
        // 3. 获取原始用户信息
        SysUser originalUser = sysUserService.getUserById(originalUserId);
        if (originalUser == null) {
            throw new BusinessException("原始用户不存在");
        }
        
        // 4. 切换回原始用户
        StpUtil.logout(); // 先登出当前用户
        StpUtil.login(originalUserId); // 登录为原始用户
        
        // 5. 清除原始用户ID标记
        StpUtil.getSession().delete("originalUserId");
        StpUtil.getSession().delete("originalUsername");
        
        // 6. 获取原始用户的租户信息
        List<com.ldjt.emp.vo.user.UserTenantVO> tenants = 
            userTenantService.getUserTenants(originalUserId);
        
        SysTenant tenant = null;
        if (tenants != null && !tenants.isEmpty()) {
            com.ldjt.emp.vo.user.UserTenantVO primaryTenant = tenants.stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsPrimary()))
                .findFirst()
                .orElse(tenants.get(0));
            
            tenant = tenantService.getById(primaryTenant.getTenantId());
            
            // 设置租户上下文
            StpUtil.getSession().set("tenantId", tenant.getId());
            StpUtil.getSession().set("tenantCode", tenant.getTenantCode());
            StpUtil.getSession().set("tenantName", tenant.getTenantName());
        }
        
        // 7. 获取新的 token
        String newToken = StpUtil.getTokenValue();
        long expires = StpUtil.getTokenTimeout();
        
        // 8. 记录日志
        log.info("切换回原始账号: originalUser={}, originalUserId={}", 
            originalUsername, originalUserId);
        
        // 9. 构建响应（包含新token）
        LoginResponse response = new LoginResponse();
        response.setToken(newToken);
        response.setExpires(expires);
        response.setUser(buildUserInfo(originalUser, tenant, originalUser.getLoginIp()));
        
        return response;
    }
}
