package com.ldjt.emp.dto.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 模板邮件发送DTO
 *
 * @author kiro
 */
@Data
@Schema(description = "模板邮件发送DTO")
public class MailTemplateSendDTO {

    @Schema(description = "模板编码")
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "收件人邮箱列表")
    @NotEmpty(message = "收件人不能为空")
    private List<String> toEmails;

    @Schema(description = "抄送人邮箱列表")
    private List<String> ccEmails;

    @Schema(description = "密送人邮箱列表")
    private List<String> bccEmails;

    @Schema(description = "模板变量")
    private Map<String, Object> variables;

    @Schema(description = "附件路径列表")
    private List<String> attachments;

    @Schema(description = "邮件配置ID")
    private Long configId;
}
