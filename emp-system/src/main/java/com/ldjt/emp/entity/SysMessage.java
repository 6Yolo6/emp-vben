package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站内消息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_message")
@Schema(description = "站内消息")
public class SysMessage extends TenantBaseEntity {

    @Id(keyType = KeyType.Auto)
    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型：1-系统通知 2-业务消息 3-预警消息")
    private Integer messageType;

    @Schema(description = "发送人ID")
    private Long senderId;

    @Schema(description = "发送人姓名")
    private String senderName;

    @Schema(description = "接收人类型：1-指定用户 2-指定角色 3-全体用户")
    private Integer receiverType;

    @Schema(description = "接收人ID列表，逗号分隔")
    private String receiverIds;

    @Schema(description = "接收角色ID列表，逗号分隔")
    private String receiverRoleIds;

    @Schema(description = "状态：0-草稿 1-已发送")
    private Integer status;
}
