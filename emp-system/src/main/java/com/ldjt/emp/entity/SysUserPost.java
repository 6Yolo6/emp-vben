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
}
