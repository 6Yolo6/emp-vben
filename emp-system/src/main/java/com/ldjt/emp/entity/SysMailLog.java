package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 邮件发送记录实体
 *
 * @author kiro
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_mail_log")
@Schema(description = "邮件发送记录实体")
public class SysMailLog extends TenantBaseEntity {

    @Id(keyType = KeyType.Auto)
    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "邮件配置ID")
    private Long configId;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "收件人")
    private String toEmails;

    @Schema(description = "抄送人")
    private String ccEmails;

    @Schema(description = "密送人")
    private String bccEmails;

    @Schema(description = "邮件主题")
    private String subject;

    @Schema(description = "邮件内容")
    private String content;

    @Schema(description = "附件信息")
    private String attachments;

    @Schema(description = "发送状态(0待发送 1成功 2失败)")
    private Integer sendStatus;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
}
