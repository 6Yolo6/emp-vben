package com.ldjt.emp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.entity.SysDictData;
import com.ldjt.emp.entity.SysDictType;
import com.ldjt.emp.service.SysDictService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/system/dict")
@Tag(name = "字典管理", description = "字典类型和字典数据管理")
public class SysDictController {
    
    @Autowired
    private SysDictService dictService;
    
    // ========== 字典类型管理 ==========
    
    @GetMapping("/type/page")
    @Operation(summary = "分页查询字典类型")
    @SaCheckPermission("system:dict:query")
    public Result<Page<SysDictType>> pageDictTypes(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "字典名称") @RequestParam(required = false) String dictName,
            @Parameter(description = "字典类型") @RequestParam(required = false) String dictType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return Result.success(dictService.pageDictTypes(pageNum, pageSize, dictName, dictType, status));
    }
    
    @GetMapping("/type/{id}")
    @Operation(summary = "根据ID查询字典类型")
    @SaCheckPermission("system:dict:query")
    public Result<SysDictType> getDictTypeById(@Parameter(description = "字典类型ID") @PathVariable Long id) {
        return Result.success(dictService.getById(id));
    }
    
    @PostMapping("/type")
    @Operation(summary = "创建字典类型")
    @SaCheckPermission("system:dict:add")
    public Result<Boolean> createDictType(@Valid @RequestBody SysDictType dictType) {
        return Result.success(dictService.createDictType(dictType));
    }
    
    @PutMapping("/type")
    @Operation(summary = "更新字典类型")
    @SaCheckPermission("system:dict:edit")
    public Result<Boolean> updateDictType(@Valid @RequestBody SysDictType dictType) {
        return Result.success(dictService.updateDictType(dictType));
    }
    
    @DeleteMapping("/type/{id}")
    @Operation(summary = "删除字典类型")
    @SaCheckPermission("system:dict:delete")
    public Result<Boolean> deleteDictType(@Parameter(description = "字典类型ID") @PathVariable Long id) {
        return Result.success(dictService.deleteDictType(id));
    }
    
    @PostMapping("/type/refresh")
    @Operation(summary = "刷新字典缓存")
    @SaCheckPermission("system:dict:edit")
    public Result<Void> refreshCache() {
        dictService.refreshCache();
        return Result.success();
    }
    
    // ========== 字典数据管理 ==========
    
    @GetMapping("/data/type/{dictType}")
    @Operation(summary = "根据字典类型查询字典数据")
    public Result<List<SysDictData>> getDictDataByType(
            @Parameter(description = "字典类型") @PathVariable String dictType) {
        return Result.success(dictService.getDictDataByType(dictType));
    }
    
    @GetMapping("/data/page")
    @Operation(summary = "分页查询字典数据")
    @SaCheckPermission("system:dict:query")
    public Result<Page<SysDictData>> pageDictData(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "字典类型") @RequestParam(required = false) String dictType,
            @Parameter(description = "字典标签") @RequestParam(required = false) String dictLabel,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        return Result.success(dictService.pageDictData(pageNum, pageSize, dictType, dictLabel, status));
    }
    
    @PostMapping("/data")
    @Operation(summary = "创建字典数据")
    @SaCheckPermission("system:dict:add")
    public Result<Boolean> createDictData(@Valid @RequestBody SysDictData dictData) {
        return Result.success(dictService.createDictData(dictData));
    }
    
    @PutMapping("/data")
    @Operation(summary = "更新字典数据")
    @SaCheckPermission("system:dict:edit")
    public Result<Boolean> updateDictData(@Valid @RequestBody SysDictData dictData) {
        return Result.success(dictService.updateDictData(dictData));
    }
    
    @DeleteMapping("/data/{id}")
    @Operation(summary = "删除字典数据")
    @SaCheckPermission("system:dict:delete")
    public Result<Boolean> deleteDictData(@Parameter(description = "字典数据ID") @PathVariable Long id) {
        return Result.success(dictService.deleteDictData(id));
    }
    
    @GetMapping("/data/label")
    @Operation(summary = "根据字典类型和值获取标签")
    public Result<String> getDictLabel(
            @Parameter(description = "字典类型") @RequestParam String dictType,
            @Parameter(description = "字典值") @RequestParam String dictValue) {
        return Result.success(dictService.getDictLabel(dictType, dictValue));
    }
}
