package com.ldjt.emp.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 岗位角色关联实体
 *
 * @author emp
 */
@Data
@Table("sys_post_role")
public class SysPostRole {

    private Long postId;

    private Long roleId;
    /**
     * 租户ID（该岗位关联在哪个租户下）
     */
    private Long tenantId;
}
