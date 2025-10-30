package com.ldjt.emp.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 用户岗位关联实体
 * 
 * @author emp
 */
@Data
@Table("sys_user_post")
public class SysUserPost {
    
    private Long userId;
    
    private Long postId;
    
    /**
     * 租户ID（该岗位关联在哪个租户下）
     */
    private Long tenantId;
}
