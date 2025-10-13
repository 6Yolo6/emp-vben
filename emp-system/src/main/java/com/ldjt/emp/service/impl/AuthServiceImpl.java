package com.ldjt.emp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.LoginRequest;
import com.ldjt.emp.dto.LoginResponse;
import com.ldjt.emp.entity.SysUser;
import com.ldjt.emp.service.AuthService;
import com.ldjt.emp.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public LoginResponse login(LoginRequest loginRequest, String loginIp) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        log.info("用户登录: {}, IP: {}", username, loginIp);

        // 1. 查询用户
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 3. 验证密码
        if (!sysUserService.checkPassword(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 登录成功，生成Token
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();
        long expires = StpUtil.getTokenTimeout();

        // 5. 更新登录信息
        sysUserService.updateLoginInfo(user.getId(), loginIp);

        // 6. 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpires(expires);
        response.setUser(buildUserInfo(user, loginIp));

        log.info("用户登录成功: {}, IP: {}", username, loginIp);
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

        return buildUserInfo(user, user.getLoginIp());
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

    /**
     * 构建用户信息
     */
    private LoginResponse.UserInfo buildUserInfo(SysUser user, String loginIp) {
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

        // 获取用户角色和权限
        List<String> roles = sysUserService.getUserRoles(user.getId());
        List<String> permissions = sysUserService.getUserPermissions(user.getId());
        userInfo.setRoles(roles);
        userInfo.setPermissions(permissions);

        return userInfo;
    }
}
