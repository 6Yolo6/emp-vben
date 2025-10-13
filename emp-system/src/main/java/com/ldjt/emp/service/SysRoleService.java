package com.ldjt.emp.service;

import com.ldjt.emp.entity.SysRole;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 角色服务接口
 * 
 * @author emp
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色
     * 
     * @param page 分页对象
     * @param roleName 角色名称
     * @return 分页结果
     */
    Page<SysRole> page(Page<SysRole> page, String roleName);
}
