package com.ldjt.emp.dto.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统参数查询DTO
 *
 * @author system
 */
@Data
@Schema(description = "系统参数查询DTO")
public class ConfigQueryDTO {

    @Schema(description = "参数名称（模糊查询）")
    private String configName;

    @Schema(description = "参数键名（模糊查询）")
    private String configKey;

    @Schema(description = "参数类型：0=系统内置 1=用户自定义")
    private Integer configType;

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页数量")
    private Integer pageSize = 10;
}
