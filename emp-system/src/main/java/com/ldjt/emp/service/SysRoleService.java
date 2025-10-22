package com.ldjt.emp.service;

import com.ldjt.emp.entity.SysRole;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 分配角色菜单权限
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    boolean assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 获取角色的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 分配角色自定义部门
     *
     * @param roleId 角色ID
     * @param deptIds 部门ID列表
     * @return 是否成功
     */
    boolean assignDepts(Long roleId, List<Long> deptIds);

    /**
     * 获取角色的自定义部门ID列表
     *
     * @param roleId 角色ID
     * @return 部门ID列表
     */
    List<Long> getRoleDeptIds(Long roleId);

    @Transactional(rollbackFor = Exception.class)
    boolean removeById(Long id);
}
