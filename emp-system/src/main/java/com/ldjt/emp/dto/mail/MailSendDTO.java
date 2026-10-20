package com.ldjt.emp.dto.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 邮件发送DTO
 *
 * @author kiro
 */
@Data
@Schema(description = "邮件发送DTO")
public class MailSendDTO {

    @Schema(description = "收件人邮箱列表")
    @NotEmpty(message = "收件人不能为空")
    private List<String> toEmails;

    @Schema(description = "抄送人邮箱列表")
    private List<String> ccEmails;

    @Schema(description = "密送人邮箱列表")
    private List<String> bccEmails;

    @Schema(description = "邮件主题")
    @NotBlank(message = "邮件主题不能为空")
    private String subject;

    @Schema(description = "邮件内容")
    @NotBlank(message = "邮件内容不能为空")
    private String content;

    @Schema(description = "是否HTML格式")
    private Boolean isHtml = true;

    @Schema(description = "附件文件路径列表")
    private List<String> attachments;

    @Schema(description = "邮件配置ID")
    private Long configId;
}
