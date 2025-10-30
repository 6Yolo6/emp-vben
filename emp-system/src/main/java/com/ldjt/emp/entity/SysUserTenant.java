package com.ldjt.emp.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户租户关联实体
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Data
@Table("sys_user_tenant")
@Schema(description = "用户租户关联")
public class SysUserTenant implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
    
    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;
    
    /**
     * 是否为用户的主要单位
     */
    @Schema(description = "是否为主要单位")
    private Boolean isPrimary;
    
    /**
     * 用户加入该单位的时间
     */
    @Schema(description = "加入时间")
    private Date joinTime;
    
    /**
     * 状态(1在职 2辞职 3调出 4退休)
     */
    @Schema(description = "状态")
    private Integer status;
    
    /**
     * 部门ID（该用户在此租户下的部门）
     */
    @Schema(description = "部门ID")
    private Long deptId;
    
    /**
     * 主岗位ID（该用户在此租户下的主岗位）
     */
    @Schema(description = "主岗位ID")
    private Long mainPostId;
}
