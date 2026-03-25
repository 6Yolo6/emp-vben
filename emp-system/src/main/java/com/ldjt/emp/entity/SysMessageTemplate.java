package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_message_template")
@Schema(description = "消息模板")
public class SysMessageTemplate extends TenantBaseEntity {

    @Id(keyType = KeyType.Auto)
    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板类型：1-系统通知 2-业务消息 3-预警消息")
    private Integer templateType;

    @Schema(description = "标题模板")
    private String titleTemplate;

    @Schema(description = "内容模板")
    private String contentTemplate;

    @Schema(description = "变量定义，JSON格式")
    private String variables;

    @Schema(description = "模板描述")
    private String description;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

    @Schema(description = "版本号")
    private Integer version;
}
