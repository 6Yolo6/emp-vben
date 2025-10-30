package com.ldjt.emp.framework.mybatis;

import com.ldjt.emp.common.core.domain.BaseEntity;
import com.ldjt.emp.common.entity.TenantEntity;
import com.ldjt.emp.framework.security.SecurityUtils;
import com.ldjt.emp.framework.tenant.TenantContextHolder;
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
            // 如果未登录，使用系统用户ID（1）
            if (userId == null) {
                userId = 1L;
            }
            LocalDateTime now = LocalDateTime.now();

            // 设置创建信息（必须设置，不能为null）
            baseEntity.setCreateBy(userId);
            baseEntity.setCreateTime(now);

            // 设置更新信息
            baseEntity.setUpdateBy(userId);
            baseEntity.setUpdateTime(now);
        }
        
        // 自动设置租户ID
        if (entity instanceof TenantEntity) {
            TenantEntity tenantEntity = (TenantEntity) entity;
            if (tenantEntity.getTenantId() == null) {
                Long tenantId = TenantContextHolder.getTenantId();
                if (tenantId != null) {
                    tenantEntity.setTenantId(tenantId);
                }
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
            Long userId = SecurityUtils.getUserId();
            // 如果未登录，使用系统用户ID（1）
            if (userId == null) {
                userId = 1L;
            }
            baseEntity.setUpdateBy(userId);
            baseEntity.setUpdateTime(LocalDateTime.now());
        }
    }
}
