package com.ldjt.emp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.entity.SysMessageTemplate;
import com.ldjt.emp.service.SysMessageTemplateService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息模板控制器
 */
@Tag(name = "消息模板管理")
@RestController
@RequestMapping("/system/message/template")
@RequiredArgsConstructor
public class SysMessageTemplateController {

    private final SysMessageTemplateService templateService;

    @Operation(summary = "创建模板")
    @PostMapping
    @SaCheckPermission("system:message:template:create")
    public Result<Void> createTemplate(@Valid @RequestBody SysMessageTemplate template) {
        templateService.createTemplate(template);
        return Result.success();
    }

    @Operation(summary = "更新模板")
    @PutMapping
    @SaCheckPermission("system:message:template:update")
    public Result<Void> updateTemplate(@Valid @RequestBody SysMessageTemplate template) {
        templateService.updateTemplate(template);
        return Result.success();
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:message:template:delete")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return Result.success();
    }

    @Operation(summary = "查询模板详情")
    @GetMapping("/{id}")
    public Result<SysMessageTemplate> getTemplateById(@PathVariable Long id) {
        return Result.success(templateService.getTemplateById(id));
    }

    @Operation(summary = "查询模板列表")
    @GetMapping("/page")
    public Result<Page<SysMessageTemplate>> getTemplatePage(
            @Parameter(description = "模板名称") @RequestParam(required = false) String templateName,
            @Parameter(description = "模板类型") @RequestParam(required = false) Integer templateType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        return Result.success(templateService.getTemplatePage(templateName, templateType, pageNum, pageSize));
    }

    @Operation(summary = "获取所有启用的模板列表（用于下拉选择）")
    @GetMapping("/list")
    public Result<List<SysMessageTemplate>> getTemplateList(
            @Parameter(description = "状态（1-启用，0-禁用）") @RequestParam(required = false, defaultValue = "1") Integer status
    ) {
        return Result.success(templateService.getTemplateList(status));
    }

    @Operation(summary = "根据模板发送消息")
    @PostMapping("/send")
    @SaCheckPermission("system:message:send")
    public Result<Void> sendMessageByTemplate(@RequestBody Map<String, Object> params) {
        String templateCode = (String) params.get("templateCode");
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>) params.get("variables");
        Integer receiverType = (Integer) params.get("receiverType");
        @SuppressWarnings("unchecked")
        List<Long> receiverIds = (List<Long>) params.get("receiverIds");
        @SuppressWarnings("unchecked")
        List<Long> receiverRoleIds = (List<Long>) params.get("receiverRoleIds");

        templateService.sendMessageByTemplate(templateCode, variables, receiverType, receiverIds, receiverRoleIds);
        return Result.success();
    }
}
