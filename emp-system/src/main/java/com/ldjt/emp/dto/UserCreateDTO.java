package com.ldjt.emp.dto;

import com.ldjt.emp.dto.user.UserTenantDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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

    @Schema(description = "性别(0未知 1男 2女)")
    private Integer sex = 0;

    @Schema(description = "租户配置列表", required = true)
    private List<UserTenantDTO> tenants;

    @Schema(description = "主岗位ID（从所有租户的岗位中选择一个）")
    private Long mainPostId;

    @Schema(description = "备注")
    private String remark;

    // ========== 以下字段保留用于向后兼容（单租户模式） ==========

    @Schema(description = "部门ID（兼容字段，建议使用tenants）")
    private Long deptId;

    @Schema(description = "岗位ID列表（兼容字段，建议使用tenants）")
    private List<Long> postIds;

    @Schema(description = "角色ID列表（兼容字段，建议使用tenants）")
    private List<Long> roleIds;

    @Schema(description = "状态(0停用 1正常)（兼容字段，建议使用tenants）")
    private Integer status = 1;
}
