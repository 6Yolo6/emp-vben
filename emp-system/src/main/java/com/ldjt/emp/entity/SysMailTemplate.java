package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 邮件模板实体
 *
 * @author kiro
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_mail_template")
@Schema(description = "邮件模板实体")
public class SysMailTemplate extends TenantBaseEntity {

    @Id(keyType = KeyType.Auto)
    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "邮件主题")
    private String subject;

    @Schema(description = "邮件内容")
    private String content;

    @Schema(description = "模板类型(1HTML 2纯文本)")
    private Integer templateType;

    @Schema(description = "模板变量")
    private String variables;

    @Schema(description = "状态(0停用 1正常)")
    private Integer status;
}
