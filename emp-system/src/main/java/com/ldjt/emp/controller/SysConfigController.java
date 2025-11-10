package com.ldjt.emp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.dto.config.ConfigCreateDTO;
import com.ldjt.emp.dto.config.ConfigQueryDTO;
import com.ldjt.emp.dto.config.ConfigUpdateDTO;
import com.ldjt.emp.service.SysConfigService;
import com.ldjt.emp.vo.config.ConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统参数配置控制器
 *
 * @author system
 */
@Tag(name = "系统参数配置")
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService configService;

    @Operation(summary = "分页查询系统参数")
    @GetMapping("/page")
    @SaCheckPermission("system:config:query")
    public Result<Page<ConfigVO>> page(@Valid ConfigQueryDTO queryDTO) {
        return Result.success(configService.page(queryDTO));
    }

    @Operation(summary = "根据ID查询系统参数")
    @GetMapping("/{id}")
    @SaCheckPermission("system:config:query")
    public Result<ConfigVO> getById(@Parameter(description = "参数ID") @PathVariable Long id) {
        return Result.success(configService.getById(id));
    }

    @Operation(summary = "根据参数键名查询参数值")
    @GetMapping("/key/{configKey}")
    @SaCheckPermission("system:config:query")
    public Result<String> getByKey(@Parameter(description = "参数键名") @PathVariable String configKey) {
        return Result.success(configService.getConfigValueByKey(configKey));
    }

    @Operation(summary = "创建系统参数")
    @PostMapping
    @SaCheckPermission("system:config:add")
    public Result<Long> create(@Valid @RequestBody ConfigCreateDTO createDTO) {
        return Result.success(configService.create(createDTO));
    }

    @Operation(summary = "更新系统参数")
    @PutMapping
    @SaCheckPermission("system:config:edit")
    public Result<Void> update(@Valid @RequestBody ConfigUpdateDTO updateDTO) {
        configService.update(updateDTO);
        return Result.success();
    }

    @Operation(summary = "删除系统参数")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:config:remove")
    public Result<Void> delete(@Parameter(description = "参数ID") @PathVariable Long id) {
        configService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除系统参数")
    @DeleteMapping("/batch")
    @SaCheckPermission("system:config:remove")
    public Result<Void> deleteBatch(@Parameter(description = "参数ID数组") @RequestBody Long[] ids) {
        configService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "刷新参数缓存")
    @PostMapping("/refresh")
    @SaCheckPermission("system:config:edit")
    public Result<Void> refreshCache() {
        configService.refreshCache();
        return Result.success();
    }

    @Operation(summary = "清空参数缓存")
    @DeleteMapping("/cache")
    @SaCheckPermission("system:config:edit")
    public Result<Void> clearCache() {
        configService.clearCache();
        return Result.success();
    }
}
