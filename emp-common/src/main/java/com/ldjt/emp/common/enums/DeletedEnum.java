package com.ldjt.emp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 删除标记枚举
 *
 * @author EMP
 */
@Getter
@AllArgsConstructor
public enum DeletedEnum {

    /**
     * 未删除
     */
    NOT_DELETED(0, "未删除"),

    /**
     * 已删除
     */
    DELETED(1, "已删除");

    /**
     * 删除标记
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据code获取枚举
     *
     * @param code 删除标记
     * @return 枚举
     */
    public static DeletedEnum getByCode(Integer code) {
        for (DeletedEnum deletedEnum : DeletedEnum.values()) {
            if (deletedEnum.getCode().equals(code)) {
                return deletedEnum;
            }
        }
        return null;
    }
}
