package com.ldjt.emp.dto.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户查询DTO
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Data
@Schema(description = "租户查询DTO")
public class TenantQueryDTO {
    
    @Schema(description = "租户编码")
    private String tenantCode;
    
    @Schema(description = "租户名称")
    private String tenantName;
    
    @Schema(description = "租户类型")
    private String tenantType;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "页码")
    private Integer pageNum = 1;
    
    @Schema(description = "每页数量")
    private Integer pageSize = 10;
}
