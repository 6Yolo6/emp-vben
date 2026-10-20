package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 邮件配置实体
 *
 * @author kiro
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_mail_config")
@Schema(description = "邮件配置实体")
public class SysMailConfig extends TenantBaseEntity {

    @Id(keyType = KeyType.Auto)
    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "SMTP服务器")
    private String smtpHost;

    @Schema(description = "SMTP端口")
    private Integer smtpPort;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码(加密)")
    private String password;

    @Schema(description = "发件人名称")
    private String fromName;

    @Schema(description = "是否启用SSL")
    private Boolean sslEnable;

    @Schema(description = "是否默认配置")
    private Boolean isDefault;

    @Schema(description = "状态(0停用 1正常)")
    private Integer status;
}
