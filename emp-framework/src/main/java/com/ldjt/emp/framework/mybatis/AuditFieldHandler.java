package com.ldjt.emp.framework.mybatis;

import com.ldjt.emp.common.core.domain.BaseEntity;
import com.ldjt.emp.framework.security.SecurityUtils;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Flex审计字段自动填充处理器
 * 
 * @author emp
 */
@Component
public class AuditFieldHandler implements InsertListener, UpdateListener {

    /**
     * 插入时自动填充
     */
    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            Long userId = SecurityUtils.getUserId();
            LocalDateTime now = LocalDateTime.now();

            // 设置创建信息
            if (baseEntity.getCreateBy() == null) {
                baseEntity.setCreateBy(userId);
            }
            if (baseEntity.getCreateTime() == null) {
                baseEntity.setCreateTime(now);
            }

            // 设置更新信息
            if (baseEntity.getUpdateBy() == null) {
                baseEntity.setUpdateBy(userId);
            }
            if (baseEntity.getUpdateTime() == null) {
                baseEntity.setUpdateTime(now);
            }
        }
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            baseEntity.setUpdateBy(SecurityUtils.getUserId());
            baseEntity.setUpdateTime(LocalDateTime.now());
        }
    }
}
