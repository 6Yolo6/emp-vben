package com.ldjt.emp.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户租户信息VO
 * 用于展示用户在某个租户下的详细信息
 * 
 * @author EMP Team
 * @since 2025-10-27
 */
@Data
@Schema(description = "用户租户信息")
public class UserTenantVO {
    
    @Schema(description = "租户ID")
    private Long tenantId;
    
    @Schema(description = "租户名称")
    private String tenantName;
    
    @Schema(description = "是否为主租户")
    private Boolean isPrimary;
    
    @Schema(description = "部门ID（该租户下的部门）")
    private Long deptId;
    
    @Schema(description = "部门名称")
    private String deptName;
    
    @Schema(description = "角色ID列表（该租户下的角色）")
    private List<Long> roleIds;
    
    @Schema(description = "角色名称列表")
    private List<String> roleNames;
    
    @Schema(description = "岗位ID列表（该租户下的岗位）")
    private List<Long> postIds;
    
    @Schema(description = "岗位名称列表")
    private List<String> postNames;
    
    @Schema(description = "主岗位ID（该租户下的主岗位）")
    private Long mainPostId;
    
    @Schema(description = "主岗位名称")
    private String mainPostName;
    
    @Schema(description = "状态(1在职 2辞职 3调出 4退休)")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
}
