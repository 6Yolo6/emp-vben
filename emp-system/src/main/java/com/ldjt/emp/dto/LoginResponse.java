package com.ldjt.emp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应DTO
 * 
 * @author emp
 */
@Data
@Schema(description = "登录响应")
public class LoginResponse {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "Token过期时间（秒）")
    private Long expires;

    @Schema(description = "用户信息")
    private UserInfo user;

    @Data
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "昵称")
        private String nickname;

        @Schema(description = "邮箱")
        private String email;

        @Schema(description = "手机号")
        private String phone;

        @Schema(description = "头像")
        private String avatar;

        @Schema(description = "部门ID")
        private Long deptId;

        @Schema(description = "部门名称")
        private String deptName;

        @Schema(description = "状态")
        private String status;

        @Schema(description = "创建时间")
        private String createTime;

        @Schema(description = "备注")
        private String remark;

        @Schema(description = "最后登录时间")
        private String lastLoginTime;

        @Schema(description = "最后登录IP")
        private String lastLoginIp;

        @Schema(description = "角色列表")
        private java.util.List<String> roles;

        @Schema(description = "权限列表")
        private java.util.List<String> permissions;

        @Schema(description = "租户ID")
        private Long tenantId;

        @Schema(description = "租户编码")
        private String tenantCode;

        @Schema(description = "租户名称")
        private String tenantName;
    }

    @Data
    @Schema(description = "Token信息")
    public static class TokenInfo {
        @Schema(description = "访问令牌")
        private String token;

        @Schema(description = "Token过期时间（秒）")
        private Long expires;
    }
}
