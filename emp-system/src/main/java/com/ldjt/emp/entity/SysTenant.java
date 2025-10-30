package com.ldjt.emp.entity;

import com.ldjt.emp.common.core.domain.BaseEntity;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 租户(单位)实体
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_tenant")
@Schema(description = "租户(单位)")
public class SysTenant extends BaseEntity {
    
    /**
     * 租户编码(唯一标识,用于URL识别)
     */
    @Schema(description = "租户编码")
    private String tenantCode;
    
    /**
     * 租户名称
     */
    @Schema(description = "租户名称")
    private String tenantName;
    
    /**
     * 租户类型: company-公司, group-集团, branch-分支机构
     */
    @Schema(description = "租户类型")
    private String tenantType;
    
    /**
     * 联系人姓名
     */
    @Schema(description = "联系人姓名")
    private String contactName;
    
    /**
     * 联系人电话
     */
    @Schema(description = "联系人电话")
    private String contactPhone;
    
    /**
     * 联系人邮箱
     */
    @Schema(description = "联系人邮箱")
    private String contactEmail;
    
    /**
     * 独立域名
     */
    @Schema(description = "独立域名")
    private String domain;
    
    /**
     * 租户Logo
     */
    @Schema(description = "租户Logo")
    private String logo;
    
    /**
     * 租户过期时间
     */
    @Schema(description = "租户过期时间")
    private Date expireTime;
    
    /**
     * 账号数量限制
     */
    @Schema(description = "账号数量限制")
    private Integer accountLimit;
    
    /**
     * 存储空间限制(字节)
     */
    @Schema(description = "存储空间限制")
    private Long storageLimit;
    
    /**
     * 状态(0停用 1正常)
     */
    @Schema(description = "状态")
    private Integer status;
    
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
