package com.ldjt.emp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户更新DTO
 * 
 * @author emp
 */
@Data
@Schema(description = "用户更新请求")
public class UserUpdateDTO {
    
    @Schema(description = "用户ID", required = true)
    private Long id;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String mobile;
    
    @Schema(description = "部门ID")
    private Long deptId;
    
    @Schema(description = "备注")
    private String remark;
}
