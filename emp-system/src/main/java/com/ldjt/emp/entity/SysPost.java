package com.ldjt.emp.entity;

import com.ldjt.emp.common.core.domain.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位实体
 * 
 * @author emp
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_post")
public class SysPost extends BaseEntity {
    
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private String postCode;
    
    private String postName;
    
    private Integer postSort;
    
    private Integer status;
    
    private String remark;
}
