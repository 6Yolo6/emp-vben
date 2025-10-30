package com.ldjt.emp.dto;

import com.ldjt.emp.dto.user.UserTenantDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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

    @Schema(description = "租户配置列表")
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
}
