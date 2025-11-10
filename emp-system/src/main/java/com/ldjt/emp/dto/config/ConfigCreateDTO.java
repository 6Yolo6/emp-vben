package com.ldjt.emp.dto.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 系统参数创建DTO
 *
 * @author system
 */
@Data
@Schema(description = "系统参数创建DTO")
public class ConfigCreateDTO {

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数名称不能为空")
    private String configName;

    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数键名不能为空")
    private String configKey;

    @Schema(description = "参数键值")
    private String configValue;

    @Schema(description = "参数类型：0=系统内置 1=用户自定义", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "参数类型不能为空")
    private Integer configType;

    @Schema(description = "备注")
    private String remark;
}
