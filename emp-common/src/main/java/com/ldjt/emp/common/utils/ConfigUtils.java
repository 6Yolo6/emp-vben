package com.ldjt.emp.common.utils;

import cn.hutool.extra.spring.SpringUtil;

/**
 * 系统参数配置工具类
 *
 * @author system
 */
public class ConfigUtils {

    /**
     * 根据参数键名获取参数值
     *
     * @param configKey 参数键名
     * @return 参数值
     */
    public static String getConfigValue(String configKey) {
        try {
            // 通过 Spring 容器获取 SysConfigService Bean
            Object configService = SpringUtil.getBean("sysConfigServiceImpl");
            if (configService != null) {
                // 使用反射调用 getConfigValueByKey 方法
                return (String) configService.getClass()
                        .getMethod("getConfigValueByKey", String.class)
                        .invoke(configService, configKey);
            }
        } catch (Exception e) {
            // 忽略异常，返回 null
        }
        return null;
    }

    /**
     * 根据参数键名获取参数值，如果不存在则返回默认值
     *
     * @param configKey    参数键名
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static String getConfigValue(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultValue;
    }

    /**
     * 根据参数键名获取整数类型参数值
     *
     * @param configKey 参数键名
     * @return 参数值
     */
    public static Integer getConfigValueAsInt(String configKey) {
        String value = getConfigValue(configKey);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // 忽略异常
            }
        }
        return null;
    }

    /**
     * 根据参数键名获取整数类型参数值，如果不存在则返回默认值
     *
     * @param configKey    参数键名
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static Integer getConfigValueAsInt(String configKey, Integer defaultValue) {
        Integer value = getConfigValueAsInt(configKey);
        return value != null ? value : defaultValue;
    }

    /**
     * 根据参数键名获取布尔类型参数值
     *
     * @param configKey 参数键名
     * @return 参数值
     */
    public static Boolean getConfigValueAsBoolean(String configKey) {
        String value = getConfigValue(configKey);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return null;
    }

    /**
     * 根据参数键名获取布尔类型参数值，如果不存在则返回默认值
     *
     * @param configKey    参数键名
     * @param defaultValue 默认值
     * @return 参数值
     */
    public static Boolean getConfigValueAsBoolean(String configKey, Boolean defaultValue) {
        Boolean value = getConfigValueAsBoolean(configKey);
        return value != null ? value : defaultValue;
    }
}
