package com.ldjt.emp.service.impl;

import com.ldjt.emp.entity.SysRole;
import com.ldjt.emp.mapper.SysRoleMapper;
import com.ldjt.emp.service.SysRoleService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import static com.ldjt.emp.entity.table.SysRoleTableDef.SYS_ROLE;

/**
 * 角色服务实现类
 * 
 * @author emp
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public Page<SysRole> page(Page<SysRole> page, String roleName) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_ROLE.DELETED.eq(0))
                .and(SYS_ROLE.ROLE_NAME.like(roleName, roleName != null))
                .orderBy(SYS_ROLE.ROLE_SORT.asc());
        
        return this.page(page, queryWrapper);
    }
}
