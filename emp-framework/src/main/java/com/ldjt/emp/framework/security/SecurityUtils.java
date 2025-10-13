package com.ldjt.emp.framework.security;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 安全工具类
 * 
 * @author emp
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     * 
     * @return 用户ID，未登录返回null
     */
    public static Long getUserId() {
        try {
            if (StpUtil.isLogin()) {
                return Long.valueOf(StpUtil.getLoginIdAsString());
            }
        } catch (Exception e) {
            // 忽略异常，返回null
        }
        return null;
    }

    /**
     * 获取当前登录用户ID（必须登录）
     * 
     * @return 用户ID
     * @throws RuntimeException 未登录时抛出异常
     */
    public static Long getRequiredUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return userId;
    }

    /**
     * 判断是否已登录
     * 
     * @return true-已登录，false-未登录
     */
    public static boolean isLogin() {
        try {
            return StpUtil.isLogin();
        } catch (Exception e) {
            return false;
        }
    }
}
