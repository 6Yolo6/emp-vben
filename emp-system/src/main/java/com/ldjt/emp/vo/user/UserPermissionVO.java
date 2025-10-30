package com.ldjt.emp.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户权限信息VO
 */
@Data
@Schema(description = "用户权限信息")
public class UserPermissionVO {
    
    @Schema(description = "用户的所有权限（已去重，直接分配优先级最高）")
    private List<PermissionItem> permissions;
    
    /**
     * 权限项
     */
    @Data
    @Schema(description = "权限项")
    public static class PermissionItem {
        @Schema(description = "菜单ID")
        private Long menuId;
        
        @Schema(description = "父菜单ID")
        private Long parentId;
        
        @Schema(description = "菜单名称")
        private String menuName;
        
        @Schema(description = "菜单类型")
        private String menuType;
        
        @Schema(description = "权限标识")
        private String perms;
        
        @Schema(description = "路由地址")
        private String path;
        
        @Schema(description = "显示顺序")
        private Integer orderNum;
        
        @Schema(description = "来源类型：direct-直接分配, post-岗位, role-角色")
        private String sourceType;
        
        @Schema(description = "来源名称")
        private String sourceName;
    }
}
