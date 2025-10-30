package com.ldjt.emp.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户VO - 用于前端展示
 * 包含用户基本信息及关联的部门、岗位、角色信息
 * 
 * @author EMP Team
 */
@Data
@Schema(description = "用户VO")
public class UserVO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String mobile;
    
    @Schema(description = "性别(0未知 1男 2女)")
    private Integer sex;
    
    @Schema(description = "性别名称")
    private String sexName;
    
    @Schema(description = "头像")
    private String avatar;
    
    @Schema(description = "部门ID")
    private Long deptId;
    
    @Schema(description = "部门名称")
    private String deptName;
    
    @Schema(description = "状态(0停用 1正常)")
    private Integer status;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "岗位ID列表")
    private List<Long> postIds;
    
    @Schema(description = "岗位名称列表")
    private List<String> postNames;
    
    @Schema(description = "主岗位ID")
    private Long mainPostId;
    
    @Schema(description = "主岗位名称")
    private String mainPostName;
    
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
    
    @Schema(description = "角色名称列表")
    private List<String> roleNames;
    
    @Schema(description = "租户编码（当前租户）")
    private String tenantCode;
    
    @Schema(description = "租户名称（当前租户）")
    private String tenantName;
    
    @Schema(description = "用户的所有租户配置")
    private List<UserTenantVO> tenants;
    
    // ========== 主单位信息（用于列表显示） ==========
    
    @Schema(description = "主单位名称")
    private String primaryTenantName;
    
    @Schema(description = "主单位部门名称")
    private String primaryDeptName;
    
    @Schema(description = "主单位岗位名称列表")
    private List<String> primaryPostNames;
    
    @Schema(description = "主单位主岗位名称")
    private String primaryMainPostName;
    
    @Schema(description = "主单位状态")
    private Integer primaryStatus;
    
    @Schema(description = "主单位状态名称")
    private String primaryStatusName;
}
