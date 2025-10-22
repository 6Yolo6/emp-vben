package com.ldjt.emp.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 角色菜单关联实体
 * 
 * @author emp
 */
@Data
@Table("sys_role_menu")
public class SysRoleMenu {
    
    private Long roleId;
    
    private Long menuId;
}
