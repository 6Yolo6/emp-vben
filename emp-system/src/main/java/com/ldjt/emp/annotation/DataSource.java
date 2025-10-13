package com.ldjt.emp.annotation;

import com.ldjt.emp.config.DataSourceContextHolder;

import java.lang.annotation.*;

/**
 * 数据源注解
 * 
 * @author emp
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    
    /**
     * 数据源类型
     */
    DataSourceContextHolder.DataSourceType value() default DataSourceContextHolder.DataSourceType.MASTER;
}
