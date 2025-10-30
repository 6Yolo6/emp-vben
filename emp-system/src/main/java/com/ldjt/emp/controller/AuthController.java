package com.ldjt.emp.controller;

import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.common.utils.ServletUtils;
import com.ldjt.emp.dto.LoginRequest;
import com.ldjt.emp.dto.LoginResponse;
import com.ldjt.emp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author EMP Team
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "用户登录、登出、获取用户信息等认证相关接口")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                       HttpServletRequest request) {
        String loginIp = ServletUtils.getClientIp(request);
        LoginResponse response = authService.login(loginRequest, loginIp);
        return Result.success(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/getInfo")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public Result<LoginResponse.UserInfo> getUserInfo() {
        LoginResponse.UserInfo userInfo = authService.getCurrentUserInfo();
        return Result.success(userInfo);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "刷新访问令牌")
    public Result<LoginResponse.TokenInfo> refreshToken() {
        LoginResponse.TokenInfo tokenInfo = authService.refreshToken();
        return Result.success(tokenInfo);
    }

    @PostMapping("/switch-tenant")
    @Operation(summary = "切换租户", description = "切换到其他租户")
    public Result<LoginResponse.UserInfo> switchTenant(
            @RequestParam(required = false) String tenantCode,
            @RequestParam(required = false) Long tenantId) {
        LoginResponse.UserInfo userInfo;
        if (tenantId != null) {
            userInfo = authService.switchTenantById(tenantId);
        } else if (tenantCode != null) {
            userInfo = authService.switchTenant(tenantCode);
        } else {
            throw new RuntimeException("租户代码或租户ID必须提供一个");
        }
        return Result.success(userInfo);
    }

    @GetMapping("/user-tenants")
    @Operation(summary = "获取用户可用租户列表", description = "根据用户名获取该用户可以登录的租户列表")
    public Result<?> getUserTenants(@RequestParam String username) {
        return Result.success(authService.getUserAvailableTenants(username));
    }

    @GetMapping("/tenants")
    @Operation(summary = "获取所有可用租户列表", description = "获取所有状态正常的租户列表供登录选择")
    public Result<?> getAllTenants() {
        return Result.success(authService.getAllAvailableTenants());
    }

    @PostMapping("/switch-to-user")
    @Operation(summary = "切换到其他用户", description = "管理员专用：切换到其他用户账号进行调试")
    public Result<LoginResponse> switchToUser(
            @RequestParam Long targetUserId,
            @RequestParam(required = false) Long tenantId) {
        LoginResponse userInfo = authService.switchToUser(targetUserId, tenantId);
        return Result.success(userInfo);
    }

    @PostMapping("/switch-back")
    @Operation(summary = "切换回原账号", description = "从其他用户账号切换回管理员账号")
    public Result<LoginResponse> switchBack() {
        LoginResponse userInfo = authService.switchBackToOriginal();
        return Result.success(userInfo);
    }
}
