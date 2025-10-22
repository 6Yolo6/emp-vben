package com.ldjt.emp.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 用户角色关联实体
 * 
 * @author emp
 */
@Data
@Table("sys_user_role")
public class SysUserRole {
    
    private Long userId;
    
    private Long roleId;
}
