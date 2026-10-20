package com.ldjt.emp.dto.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 邮件配置DTO
 *
 * @author kiro
 */
@Data
@Schema(description = "邮件配置DTO")
public class MailConfigDTO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置名称")
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    @Schema(description = "SMTP服务器")
    @NotBlank(message = "SMTP服务器不能为空")
    private String smtpHost;

    @Schema(description = "SMTP端口")
    @NotNull(message = "SMTP端口不能为空")
    private Integer smtpPort;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "发件人名称")
    private String fromName;

    @Schema(description = "是否启用SSL")
    private Boolean sslEnable = true;

    @Schema(description = "是否默认配置")
    private Boolean isDefault = false;

    @Schema(description = "状态(0停用 1正常)")
    private Integer status = 1;
}
