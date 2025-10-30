package com.ldjt.emp.service.impl;

import com.ldjt.emp.entity.SysTenant;
import com.ldjt.emp.mapper.SysTenantMapper;
import com.ldjt.emp.service.SysTenantService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.ldjt.emp.entity.table.SysTenantTableDef.SYS_TENANT;

/**
 * 租户服务实现
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Slf4j
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements SysTenantService {
    
    @Override
    @Cacheable(value = "tenant", key = "#tenantCode")
    public SysTenant getByCode(String tenantCode) {
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_TENANT.TENANT_CODE.eq(tenantCode))
                .and(SYS_TENANT.DELETED.eq(0));
        
        return getOne(query);
    }
    
    @Override
    public boolean checkTenantCodeUnique(String tenantCode, Long tenantId) {
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_TENANT.TENANT_CODE.eq(tenantCode))
                .and(SYS_TENANT.DELETED.eq(0));
        
        if (tenantId != null) {
            query.and(SYS_TENANT.ID.ne(tenantId));
        }
        
        return count(query) == 0;
    }
}
