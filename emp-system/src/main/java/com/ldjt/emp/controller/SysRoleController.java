package com.ldjt.emp.controller;

import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.entity.SysRole;
import com.ldjt.emp.service.SysRoleService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 *
 * @author emp
 */
@Slf4j
@RestController
@RequestMapping("/system/role")
@Tag(name = "角色管理", description = "角色的增删改查接口")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/page")
    @Operation(summary = "分页查询角色", description = "分页查询角色列表")
    public Result<Page<SysRole>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNumber,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "角色名称") @RequestParam(required = false) String roleName) {
        Page<SysRole> page = sysRoleService.page(new Page<>(pageNumber, pageSize), roleName);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询角色", description = "根据ID查询角色详情")
    public Result<SysRole> getById(@Parameter(description = "角色ID") @PathVariable Long id) {
        SysRole entity = sysRoleService.getById(id);
        return Result.success(entity);
    }

    @PostMapping
    @Operation(summary = "新增角色", description = "新增角色")
    public Result<Boolean> save(@Valid @RequestBody SysRole entity) {
        boolean result = sysRoleService.save(entity);
        return Result.success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "更新角色")
    public Result<Boolean> update(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody SysRole entity) {
        // 确保ID一致
        entity.setId(id);
        boolean result = sysRoleService.updateById(entity);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    public Result<Boolean> remove(@Parameter(description = "角色ID") @PathVariable Long id) {
        boolean result = sysRoleService.removeById(id);
        return Result.success(result);
    }
    
    @GetMapping("/list")
    @Operation(summary = "查询所有角色", description = "查询所有角色列表")
    public Result<java.util.List<SysRole>> list() {
        return Result.success(sysRoleService.list());
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除角色", description = "批量删除角色")
    public Result<Boolean> batchRemove(@RequestBody java.util.List<Long> ids) {
        boolean result = sysRoleService.removeByIds(ids);
        return Result.success(result);
    }
    
    @PostMapping("/{id}/menus")
    @Operation(summary = "分配角色菜单权限", description = "为角色分配菜单权限")
    public Result<Boolean> assignMenus(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @RequestBody java.util.List<Long> menuIds) {
        boolean result = sysRoleService.assignMenus(id, menuIds);
        return Result.success(result);
    }
    
    @GetMapping("/{id}/menus")
    @Operation(summary = "获取角色菜单权限", description = "获取角色已分配的菜单ID列表")
    public Result<java.util.List<Long>> getRoleMenus(@Parameter(description = "角色ID") @PathVariable Long id) {
        java.util.List<Long> menuIds = sysRoleService.getRoleMenuIds(id);
        return Result.success(menuIds);
    }
    
    @PostMapping("/{id}/depts")
    @Operation(summary = "分配角色自定义部门", description = "为角色分配自定义数据权限部门")
    public Result<Boolean> assignDepts(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @RequestBody java.util.List<Long> deptIds) {
        boolean result = sysRoleService.assignDepts(id, deptIds);
        return Result.success(result);
    }
    
    @GetMapping("/{id}/depts")
    @Operation(summary = "获取角色自定义部门", description = "获取角色已分配的自定义部门ID列表")
    public Result<java.util.List<Long>> getRoleDepts(@Parameter(description = "角色ID") @PathVariable Long id) {
        java.util.List<Long> deptIds = sysRoleService.getRoleDeptIds(id);
        return Result.success(deptIds);
    }
}
