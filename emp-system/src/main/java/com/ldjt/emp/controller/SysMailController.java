package com.ldjt.emp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.dto.mail.MailConfigDTO;
import com.ldjt.emp.dto.mail.MailSendDTO;
import com.ldjt.emp.dto.mail.MailTemplateDTO;
import com.ldjt.emp.entity.SysMailConfig;
import com.ldjt.emp.service.SysMailConfigService;
import com.ldjt.emp.service.SysMailService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 邮件管理控制器
 *
 * @author kiro
 */
@Tag(name = "邮件管理")
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class SysMailController {

    private final SysMailService mailService;
    private final SysMailConfigService mailConfigService;

    @Operation(summary = "发送邮件")
    @PostMapping("/send")
    @SaCheckPermission("system:mail:send")
    public Result<Boolean> sendMail(@Valid @RequestBody MailSendDTO mailSendDTO) {
        return Result.success(mailService.sendMail(mailSendDTO));
    }

    @Operation(summary = "使用模板发送邮件")
    @PostMapping("/send/template")
    @SaCheckPermission("system:mail:send")
    public Result<Boolean> sendTemplateMail(@Valid @RequestBody MailTemplateDTO mailTemplateDTO) {
        return Result.success(mailService.sendTemplateMail(mailTemplateDTO));
    }

    @Operation(summary = "异步发送邮件")
    @PostMapping("/send/async")
    @SaCheckPermission("system:mail:send")
    public Result<Void> sendMailAsync(@Valid @RequestBody MailSendDTO mailSendDTO) {
        mailService.sendMailAsync(mailSendDTO);
        return Result.success();
    }

    @Operation(summary = "重试发送邮件")
    @PostMapping("/retry/{logId}")
    @SaCheckPermission("system:mail:send")
    public Result<Boolean> retryMail(@Parameter(description = "邮件日志ID") @PathVariable Long logId) {
        return Result.success(mailService.retryMail(logId));
    }

    @Operation(summary = "创建邮件配置")
    @PostMapping("/config")
    @SaCheckPermission("system:mail:config:create")
    public Result<Boolean> createConfig(@Valid @RequestBody MailConfigDTO mailConfigDTO) {
        return Result.success(mailConfigService.createConfig(mailConfigDTO));
    }

    @Operation(summary = "更新邮件配置")
    @PutMapping("/config")
    @SaCheckPermission("system:mail:config:update")
    public Result<Boolean> updateConfig(@Valid @RequestBody MailConfigDTO mailConfigDTO) {
        return Result.success(mailConfigService.updateConfig(mailConfigDTO));
    }

    @Operation(summary = "删除邮件配置")
    @DeleteMapping("/config/{id}")
    @SaCheckPermission("system:mail:config:delete")
    public Result<Boolean> deleteConfig(@Parameter(description = "配置ID") @PathVariable Long id) {
        return Result.success(mailConfigService.deleteConfig(id));
    }

    @Operation(summary = "测试邮件配置")
    @PostMapping("/config/test")
    @SaCheckPermission("system:mail:config:test")
    public Result<Boolean> testConfig(
            @Parameter(description = "配置ID") @RequestParam Long id,
            @Parameter(description = "测试收件人") @RequestParam String toEmail) {
        return Result.success(mailConfigService.testConfig(id, toEmail));
    }

    @Operation(summary = "分页查询邮件配置")
    @GetMapping("/config/page")
    @SaCheckPermission("system:mail:config:query")
    public Result<Page<SysMailConfig>> pageConfig(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNumber,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "配置名称") @RequestParam(required = false) String configName) {
        Page<SysMailConfig> page = Page.of(pageNumber, pageSize);
        return Result.success(mailConfigService.pageConfig(page, configName));
    }

    @Operation(summary = "查询邮件配置详情")
    @GetMapping("/config/{id}")
    @SaCheckPermission("system:mail:config:query")
    public Result<SysMailConfig> getConfig(@Parameter(description = "配置ID") @PathVariable Long id) {
        return Result.success(mailConfigService.getById(id));
    }
}
