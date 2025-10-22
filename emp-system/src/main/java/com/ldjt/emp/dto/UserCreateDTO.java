package com.ldjt.emp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户创建DTO
 * 
 * @author emp
 */
@Data
@Schema(description = "用户创建请求")
public class UserCreateDTO {
    
    @Schema(description = "用户名", required = true)
    private String username;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "密码", required = true)
    private String password;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String mobile;
    
    @Schema(description = "部门ID")
    private Long deptId;
    
    @Schema(description = "状态(0停用 1正常)")
    private Integer status = 1;
    
    @Schema(description = "备注")
    private String remark;
}
