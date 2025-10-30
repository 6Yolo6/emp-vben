package com.ldjt.emp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户租户配置DTO
 * 用于新增/编辑用户时配置租户信息
 * 
 * @author EMP Team
 * @since 2025-10-27
 */
@Data
@Schema(description = "用户租户配置")
public class UserTenantDTO {
    
    @Schema(description = "租户ID", required = true)
    private Long tenantId;
    
    @Schema(description = "是否为主租户")
    private Boolean isPrimary;
    
    @Schema(description = "部门ID（该租户下的部门）")
    private Long deptId;
    
    @Schema(description = "角色ID列表（该租户下的角色）")
    private List<Long> roleIds;
    
    @Schema(description = "岗位ID列表（该租户下的岗位）")
    private List<Long> postIds;
    
    @Schema(description = "主岗位ID（该租户下的主岗位）")
    private Long mainPostId;
    
    @Schema(description = "状态(1在职 2辞职 3调出 4退休)")
    private Integer status = 1;
}
