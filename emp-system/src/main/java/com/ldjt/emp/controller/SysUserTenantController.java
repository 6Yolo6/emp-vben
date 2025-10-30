package com.ldjt.emp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.vo.user.UserTenantVO;
import com.ldjt.emp.service.SysUserTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户租户关联管理控制器
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Tag(name = "用户租户关联管理")
@Slf4j
@RestController
@RequestMapping("/system/user-tenant")
@RequiredArgsConstructor
public class SysUserTenantController {
    
    private final SysUserTenantService userTenantService;
    
    @Operation(summary = "获取当前用户的租户列表")
    @GetMapping("/my-tenants")
    public Result<List<UserTenantVO>> getMyTenants() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<UserTenantVO> tenants = userTenantService.getUserTenants(userId);
        return Result.success(tenants);
    }
    
    @Operation(summary = "获取用户的租户列表")
    @GetMapping("/user/{userId}")
    @SaCheckPermission("system:user:query")
    public Result<List<UserTenantVO>> getUserTenants(@PathVariable Long userId) {
        List<UserTenantVO> tenants = userTenantService.getUserTenants(userId);
        return Result.success(tenants);
    }
    
    @Operation(summary = "设置主租户")
    @PutMapping("/set-primary")
    public Result<Void> setPrimaryTenant(@RequestParam Long tenantId) {
        Long userId = StpUtil.getLoginIdAsLong();
        userTenantService.setPrimaryTenant(userId, tenantId);
        log.info("设置主租户成功: userId={}, tenantId={}", userId, tenantId);
        return Result.success();
    }
}
