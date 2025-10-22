package com.ldjt.emp.common.enums;

import lombok.Getter;

/**
 * 数据权限范围枚举
 * 
 * @author emp
 */
@Getter
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
    
    private final Integer code;
    private final String description;
    
    DataScopeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static DataScopeEnum getByCode(Integer code) {
        if (code == null) {
            return ALL;
        }
        for (DataScopeEnum scope : values()) {
            if (scope.getCode().equals(code)) {
                return scope;
            }
        }
        return ALL;
    }
}
