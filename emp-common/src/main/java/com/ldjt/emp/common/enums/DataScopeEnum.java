package com.ldjt.emp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限范围枚举
 *
 * @author EMP
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum {

    /**
     * 全部数据权限
     */
    ALL(1, "全部数据权限"),

    /**
     * 本部门数据权限
     */
    DEPT(2, "本部门数据权限"),

    /**
     * 本部门及以下数据权限
     */
    DEPT_AND_CHILD(3, "本部门及以下数据权限"),

    /**
     * 仅本人数据权限
     */
    SELF(4, "仅本人数据权限"),

    /**
     * 自定义数据权限
     */
    CUSTOM(5, "自定义数据权限");

    /**
     * 权限范围码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据code获取枚举
     *
     * @param code 权限范围码
     * @return 枚举
     */
    public static DataScopeEnum getByCode(Integer code) {
        for (DataScopeEnum dataScopeEnum : DataScopeEnum.values()) {
            if (dataScopeEnum.getCode().equals(code)) {
                return dataScopeEnum;
            }
        }
        return null;
    }
}
