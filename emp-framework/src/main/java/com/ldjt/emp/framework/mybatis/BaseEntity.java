package com.ldjt.emp.framework.mybatis;

/**
 * BaseEntity标记接口
 * 用于MyBatis-Flex审计字段自动填充
 * 
 * @author emp
 */
public interface BaseEntity {
    Long getCreateBy();
    void setCreateBy(Long createBy);
    java.time.LocalDateTime getCreateTime();
    void setCreateTime(java.time.LocalDateTime createTime);
    Long getUpdateBy();
    void setUpdateBy(Long updateBy);
    java.time.LocalDateTime getUpdateTime();
    void setUpdateTime(java.time.LocalDateTime updateTime);
}
