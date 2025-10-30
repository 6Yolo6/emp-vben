package com.ldjt.emp.dto.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * 租户更新DTO
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Data
@Schema(description = "租户更新DTO")
public class TenantUpdateDTO {
    
    @NotNull(message = "租户ID不能为空")
    @Schema(description = "租户ID")
    private Long id;
    
    @NotBlank(message = "租户编码不能为空")
    @Size(max = 50, message = "租户编码长度不能超过50个字符")
    @Schema(description = "租户编码")
    private String tenantCode;
    
    @NotBlank(message = "租户名称不能为空")
    @Size(max = 100, message = "租户名称长度不能超过100个字符")
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
}
