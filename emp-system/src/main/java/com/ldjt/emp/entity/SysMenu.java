package com.ldjt.emp.entity;

import com.ldjt.emp.common.core.domain.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体
 * 
 * @author emp
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_menu")
public class SysMenu extends BaseEntity {
    
    /**
     * 菜单ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    /**
     * 父菜单ID
     */
    private Long parentId;
    
    /**
     * 菜单名称
     */
    private String menuName;
    
    /**
     * 菜单类型(M目录 C菜单 F按钮)
     */
    private String menuType;
    
    /**
     * 路由地址
     */
    private String path;
    
    /**
     * 组件路径
     */
    private String component;
    
    /**
     * 权限标识
     */
    private String perms;
    
    /**
     * 菜单图标
     */
    private String icon;
    
    /**
     * 显示顺序
     */
    private Integer orderNum;
    
    /**
     * 显示状态(0隐藏 1显示)
     */
    private Integer visible;
    
    /**
     * 状态(0停用 1正常)
     */
    private Integer status;
    
    /**
     * 删除标志(0未删除 1已删除)
     */
    private Integer deleted;
}
