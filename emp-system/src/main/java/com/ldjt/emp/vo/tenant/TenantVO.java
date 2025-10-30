package com.ldjt.emp.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 租户VO
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Data
@Schema(description = "租户VO")
public class TenantVO {
    
    @Schema(description = "租户ID")
    private Long id;
    
    @Schema(description = "租户编码")
    private String tenantCode;
    
    @Schema(description = "租户名称")
    private String tenantName;
    
    @Schema(description = "租户类型")
    private String tenantType;
    
    @Schema(description = "联系人姓名")
    private String contactName;
    
    @Schema(description = "联系人电话")
    private String contactPhone;
    
    @Schema(description = "联系人邮箱")
    private String contactEmail;
    
    @Schema(description = "独立域名")
    private String domain;
    
    @Schema(description = "租户Logo")
    private String logo;
    
    @Schema(description = "租户过期时间")
    private Date expireTime;
    
    @Schema(description = "账号数量限制")
    private Integer accountLimit;
    
    @Schema(description = "存储空间限制")
    private Long storageLimit;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
