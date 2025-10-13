package com.ldjt.emp.config;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据源上下文持有者
 * 
 * @author emp
 */
@Slf4j
public class DataSourceContextHolder {
    
    /**
     * 数据源类型
     */
    public enum DataSourceType {
        MASTER, SLAVE
    }
    
    private static final ThreadLocal<DataSourceType> CONTEXT_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置数据源类型
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        log.debug("切换到{}数据源", dataSourceType);
        CONTEXT_HOLDER.set(dataSourceType);
    }
    
    /**
     * 获取数据源类型
     */
    public static DataSourceType getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }
    
    /**
     * 清除数据源类型
     */
    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }
}
