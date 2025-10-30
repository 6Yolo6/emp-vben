package com.ldjt.emp.controller;

import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.entity.SysDept;
import com.ldjt.emp.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author emp
 */
@Slf4j
@RestController
@RequestMapping("/system/dept")
@Tag(name = "部门管理", description = "部门的增删改查接口")
public class SysDeptController {

    @Autowired
    private SysDeptService sysDeptService;

    @GetMapping("/tree")
    @Operation(summary = "查询部门树", description = "查询所有部门的树形结构")
    public Result<List<com.ldjt.emp.dto.DeptTreeDTO>> getDeptTree() {
        List<com.ldjt.emp.dto.DeptTreeDTO> deptTree = sysDeptService.getDeptTree();
        return Result.success(deptTree);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询部门", description = "根据ID查询部门详情")
    public Result<SysDept> getById(@Parameter(description = "部门ID") @PathVariable Long id) {
        SysDept entity = sysDeptService.getById(id);
        return Result.success(entity);
    }

    @PostMapping
    @Operation(summary = "新增部门", description = "新增部门")
    public Result<Boolean> save(@Valid @RequestBody SysDept entity) {
        boolean result = sysDeptService.createDept(entity);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "更新部门", description = "更新部门")
    public Result<Boolean> update(@Valid @RequestBody SysDept entity) {
        boolean result = sysDeptService.updateDept(entity);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门", description = "根据ID删除部门")
    public Result<Boolean> remove(@Parameter(description = "部门ID") @PathVariable Long id) {
        boolean result = sysDeptService.deleteDept(id);
        return Result.success(result);
    }
    
    @GetMapping("/list")
    @Operation(summary = "查询所有部门", description = "查询所有部门列表")
    public Result<java.util.List<SysDept>> list() {
        return Result.success(sysDeptService.list());
    }
    
    @PutMapping("/{id}/move")
    @Operation(summary = "移动部门", description = "将部门移动到新的父部门下")
    public Result<Boolean> moveDept(
            @Parameter(description = "部门ID") @PathVariable Long id,
            @Parameter(description = "新的父部门ID") @RequestParam Long newParentId) {
        boolean result = sysDeptService.moveDept(id, newParentId);
        return Result.success(result);
    }
    
    @GetMapping("/tree/by-tenant")
    @Operation(summary = "根据租户ID查询部门树", description = "查询指定租户的部门树形结构")
    public Result<List<com.ldjt.emp.dto.DeptTreeDTO>> getDeptTreeByTenant(
            @Parameter(description = "租户ID") @RequestParam Long tenantId) {
        List<com.ldjt.emp.dto.DeptTreeDTO> deptTree = sysDeptService.getDeptTreeByTenant(tenantId);
        return Result.success(deptTree);
    }
}
