package com.ldjt.emp.dto.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 邮件模板DTO
 *
 * @author kiro
 */
@Data
@Schema(description = "邮件模板DTO")
public class MailTemplateDTO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板编码")
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "模板名称")
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @Schema(description = "邮件主题")
    @NotBlank(message = "邮件主题不能为空")
    private String subject;

    @Schema(description = "邮件内容")
    @NotBlank(message = "邮件内容不能为空")
    private String content;

    @Schema(description = "模板类型：1=HTML 2=纯文本")
    @NotNull(message = "模板类型不能为空")
    private Integer templateType;

    @Schema(description = "模板变量")
    private String variables;

    @Schema(description = "状态：0=停用 1=正常")
    private Integer status = 1;
}
