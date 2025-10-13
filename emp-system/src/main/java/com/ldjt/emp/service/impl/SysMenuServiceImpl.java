package com.ldjt.emp.service.impl;

import com.ldjt.emp.entity.SysMenu;
import com.ldjt.emp.mapper.SysMenuMapper;
import com.ldjt.emp.service.SysMenuService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ldjt.emp.entity.table.SysMenuTableDef.SYS_MENU;

/**
 * 菜单服务实现类
 * 
 * @author emp
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> getMenuTree() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_MENU.DELETED.eq(0))
                .orderBy(SYS_MENU.ORDER_NUM.asc());
        
        return this.list(queryWrapper);
    }

    @Override
    public List<SysMenu> getMenuByUserId(Long userId) {
        // TODO: 根据用户ID查询菜单（需要关联角色和权限）
        // 这里先返回所有菜单，后续在权限模块中实现
        return getMenuTree();
    }
}
