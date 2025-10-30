package com.ldjt.emp.common.entity;

import com.ldjt.emp.common.core.domain.BaseEntity;
import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户基础实体类
 * 包含租户ID字段和审计字段
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantBaseEntity extends BaseEntity implements TenantEntity {
    
    /**
     * 租户ID
     */
    @Column("tenant_id")
    private Long tenantId;
}
