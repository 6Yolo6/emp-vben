package com.ldjt.emp.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户消息关联实体
 */
@Data
@Table("sys_user_message")
@Schema(description = "用户消息关联")
public class SysUserMessage {

    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "消息ID")
    private Long messageId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "是否已读：0-未读 1-已读")
    private Integer isRead;

    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "删除标记：0-未删除 1-已删除")
    private Integer deleted;
}
