//package com.ldjt.emp.controller;
//
//import com.ldjt.emp.common.core.domain.Result;
//import com.ldjt.emp.entity.SysMenu;
//import com.ldjt.emp.service.SysMenuService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * 菜单管理控制器
// *
// * @author emp
// */
//@Slf4j
//@RestController
//@RequestMapping("/system/menu")
//@Tag(name = "菜单管理", description = "菜单的增删改查接口")
//public class SysMenuController {
//
//    @Autowired
//    private SysMenuService sysMenuService;
//
//    @GetMapping("/tree")
//    @Operation(summary = "查询菜单树", description = "查询所有菜单的树形结构")
//    public Result<List<SysMenu>> getMenuTree() {
//        List<SysMenu> menuTree = sysMenuService.getMenuTree();
//        return Result.success(menuTree);
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "根据ID查询菜单", description = "根据ID查询菜单详情")
//    public Result<SysMenu> getById(@Parameter(description = "菜单ID") @PathVariable Long id) {
//        SysMenu entity = sysMenuService.getById(id);
//        return Result.success(entity);
//    }
//
//    @PostMapping
//    @Operation(summary = "新增菜单", description = "新增菜单")
//    public Result<Boolean> save(@Valid @RequestBody SysMenu entity) {
//        boolean result = sysMenuService.save(entity);
//        return Result.success(result);
//    }
//
//    @PutMapping
//    @Operation(summary = "更新菜单", description = "更新菜单")
//    public Result<Boolean> update(@Valid @RequestBody SysMenu entity) {
//        boolean result = sysMenuService.updateById(entity);
//        return Result.success(result);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "删除菜单", description = "根据ID删除菜单")
//    public Result<Boolean> remove(@Parameter(description = "菜单ID") @PathVariable Long id) {
//        boolean result = sysMenuService.removeById(id);
//        return Result.success(result);
//    }
//}
