package com.ldjt.emp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 *
 * @author EMP
 */
@Getter
@AllArgsConstructor
public enum UserStatus {

    /**
     * 正常
     */
    OK(1, "正常"),

    /**
     * 停用
     */
    DISABLE(0, "停用"),

    /**
     * 删除
     */
    DELETED(2, "删除");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String info;
}
