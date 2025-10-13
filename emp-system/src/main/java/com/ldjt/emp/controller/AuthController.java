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
}
