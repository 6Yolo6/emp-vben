package com.ldjt.emp.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean工具类
 * 基于Hutool的BeanUtil进行扩展
 *
 * @author EMP
 */
public class BeanUtils extends BeanUtil {

    /**
     * Bean属性复制
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtil.copyProperties(source, target, CopyOptions.create().ignoreNullValue());
    }

    /**
     * Bean属性复制（忽略null值）
     *
     * @param source 源对象
     * @param target 目标对象类型
     * @param <T>    目标对象类型
     * @return 目标对象
     */
    public static <T> T copyProperties(Object source, Class<T> target) {
        if (source == null) {
            return null;
        }
        try {
            T targetInstance = target.getDeclaredConstructor().newInstance();
            BeanUtil.copyProperties(source, targetInstance, CopyOptions.create().ignoreNullValue());
            return targetInstance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy properties", e);
        }
    }

    /**
     * 列表Bean属性复制
     *
     * @param sourceList 源对象列表
     * @param targetClass 目标对象类型
     * @param <S>        源对象类型
     * @param <T>        目标对象类型
     * @return 目标对象列表
     */
    public static <S, T> List<T> copyListProperties(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return List.of();
        }
        return sourceList.stream()
                .map(source -> copyProperties(source, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object obj) {
        return obj == null || BeanUtil.isEmpty(obj);
    }

    /**
     * 判断对象是否不为空
     *
     * @param obj 对象
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}
