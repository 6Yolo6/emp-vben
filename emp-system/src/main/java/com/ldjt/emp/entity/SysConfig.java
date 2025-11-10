package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数配置实体
 *
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_config")
@Schema(description = "系统参数配置")
public class SysConfig extends TenantBaseEntity {

    @Schema(description = "参数名称")
    private String configName;

    @Schema(description = "参数键名")
    private String configKey;

    @Schema(description = "参数键值")
    private String configValue;

    @Schema(description = "参数类型：0=系统内置 1=用户自定义")
    private Integer configType;

    @Schema(description = "备注")
    private String remark;
}
