package com.ldjt.emp.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 角色部门关联实体
 * 用于自定义数据权限
 * 
 * @author emp
 */
@Data
@Table("sys_role_dept")
public class SysRoleDept {
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 部门ID
     */
    private Long deptId;
}
