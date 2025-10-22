package com.ldjt.emp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询DTO
 * 
 * @author emp
 */
@Data
@Schema(description = "用户查询条件")
public class UserQueryDTO {
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "手机号")
    private String mobile;
    
    @Schema(description = "状态(0停用 1正常)")
    private Integer status;
    
    @Schema(description = "部门ID")
    private Long deptId;
    
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;
}
