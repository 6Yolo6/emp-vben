package com.ldjt.emp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.dto.UserCreateDTO;
import com.ldjt.emp.dto.UserUpdateDTO;
import com.ldjt.emp.entity.SysUser;
import com.ldjt.emp.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * @author emp
 */
@Tag(name = "用户管理")
@Slf4j
@RestController
@RequestMapping("/system/user")
public class SysUserController {
    
    @Autowired
    private SysUserService userService;
    
    /**
     * 创建用户
     */
    @Operation(summary = "创建用户")
    @SaCheckPermission("system:user:add")
    @PostMapping
    public Result<Void> create(@RequestBody UserCreateDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        userService.createUser(user);
        return Result.success();
    }
    
    /**
     * 更新用户
     */
    @Operation(summary = "更新用户")
    @SaCheckPermission("system:user:edit")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setId(id);
        userService.updateUser(user);
        return Result.success();
    }
    
    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
    
    /**
     * 根据ID查询用户
     */
    @Operation(summary = "根据ID查询用户")
    @SaCheckPermission("system:user:query")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }
    
    /**
     * 查询所有用户
     */
    @Operation(summary = "查询所有用户")
    @SaCheckPermission("system:user:query")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        return Result.success(userService.listAllUsers());
    }
    
    /**
     * 更新用户状态
     */
    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }
    
    /**
     * 重置用户密码
     */
    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }
    
    /**
     * 分页查询用户
     */
    @Operation(summary = "分页查询用户")
    @SaCheckPermission("system:user:query")
    @GetMapping("/page")
    public Result<com.mybatisflex.core.paginate.Page<SysUser>> page(
            com.ldjt.emp.dto.UserPageQueryDTO queryDTO) {
        return Result.success(userService.pageQuery(queryDTO));
    }
    
    /**
     * 分配用户角色
     */
    @Operation(summary = "分配用户角色")
    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }
    
    /**
     * 获取用户角色
     */
    @Operation(summary = "获取用户角色")
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        return Result.success(userService.getUserRoleIds(id));
    }
    
    /**
     * 批量删除用户
     */
    @Operation(summary = "批量删除用户")
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> userIds) {
        userService.batchDelete(userIds);
        return Result.success();
    }
    
    /**
     * 分配用户岗位
     */
    @Operation(summary = "分配用户岗位")
    @PostMapping("/{id}/posts")
    public Result<Void> assignPosts(@PathVariable Long id, @RequestBody List<Long> postIds) {
        userService.assignPosts(id, postIds);
        return Result.success();
    }
    
    /**
     * 获取用户岗位
     */
    @Operation(summary = "获取用户岗位")
    @GetMapping("/{id}/posts")
    public Result<List<Long>> getUserPosts(@PathVariable Long id) {
        return Result.success(userService.getUserPostIds(id));
    }
}
