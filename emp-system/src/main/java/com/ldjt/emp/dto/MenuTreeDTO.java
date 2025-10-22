package com.ldjt.emp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树DTO
 *
 * @author emp
 */
@Data
@Schema(description = "菜单树")
public class MenuTreeDTO {
    
    @Schema(description = "菜单ID")
    private Long id;
    
    @Schema(description = "父菜单ID")
    private Long parentId;
    
    @Schema(description = "菜单名称")
    private String menuName;
    
    @Schema(description = "菜单类型(M目录 C菜单 F按钮)")
    private String menuType;
    
    @Schema(description = "路由地址")
    private String path;
    
    @Schema(description = "组件路径")
    private String component;
    
    @Schema(description = "权限标识")
    private String perms;
    
    @Schema(description = "菜单图标")
    private String icon;
    
    @Schema(description = "显示顺序")
    private Integer orderNum;
    
    @Schema(description = "显示状态(0隐藏 1显示)")
    private Integer visible;
    
    @Schema(description = "状态(0停用 1正常)")
    private Integer status;
    
    @Schema(description = "子菜单")
    private List<MenuTreeDTO> children = new ArrayList<>();
}
