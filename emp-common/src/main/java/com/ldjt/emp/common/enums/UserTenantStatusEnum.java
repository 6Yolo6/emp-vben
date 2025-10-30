package com.ldjt.emp.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户在单位中的状态枚举
 * 
 * @author EMP Team
 * @since 2025-10-27
 */
@Getter
@AllArgsConstructor
public enum UserTenantStatusEnum {
    
    /**
     * 在职
     */
    ON_JOB(1, "在职"),
    
    /**
     * 辞职
     */
    RESIGNED(2, "辞职"),
    
    /**
     * 调出
     */
    TRANSFERRED(3, "调出"),
    
    /**
     * 退休
     */
    RETIRED(4, "退休");
    
    private final Integer code;
    private final String desc;
    
    /**
     * 根据code获取描述
     */
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserTenantStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDesc();
            }
        }
        return null;
    }
    
    /**
     * 根据code获取枚举
     */
    public static UserTenantStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserTenantStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
