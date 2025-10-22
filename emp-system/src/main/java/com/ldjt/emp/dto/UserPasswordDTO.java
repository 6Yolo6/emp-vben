package com.ldjt.emp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户密码重置DTO
 *
 * @author emp
 */
@Data
public class UserPasswordDTO {

    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    private String confirmPassword;
}
