package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 * 
 * @author emp
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_role")
public class SysRole extends TenantBaseEntity {
    
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private String roleName;
    
    private String roleKey;
    
    private Integer roleSort;
    
    private Integer dataScope;
    
    private Boolean menuCheckStrictly;
    
    private Boolean deptCheckStrictly;
    
    private Integer status;
    
    private String remark;
}
