package com.ldjt.emp.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 消息发送 DTO
 */
@Data
@Schema(description = "消息发送DTO")
public class MessageSendDTO {

    @NotBlank(message = "消息标题不能为空")
    @Schema(description = "消息标题")
    private String title;

    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;

    @NotNull(message = "消息类型不能为空")
    @Schema(description = "消息类型：1-系统通知 2-业务消息 3-预警消息")
    private Integer messageType;

    @NotNull(message = "接收人类型不能为空")
    @Schema(description = "接收人类型：1-指定用户 2-指定角色 3-全体用户")
    private Integer receiverType;

    @Schema(description = "接收人ID列表")
    private List<Long> receiverIds;

    @Schema(description = "接收角色ID列表")
    private List<Long> receiverRoleIds;
}
