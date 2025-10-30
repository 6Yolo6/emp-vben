package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户菜单权限关联实体
 * 用于存储直接分配给用户的菜单权限（不通过角色或岗位）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_user_menu")
public class SysUserMenu extends TenantBaseEntity {
    
    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 菜单ID
     */
    private Long menuId;
}
