package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体
 * 
 * @author emp
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_dept")
public class SysDept extends TenantBaseEntity {
    
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private Long parentId;
    
    private String ancestors;
    
    private String deptName;
    
    private Integer orderNum;
    
    private String leader;
    
    private String phone;
    
    private String email;
    
    private Integer status;
    
    private String remark;
}
