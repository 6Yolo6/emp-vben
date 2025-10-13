package com.ldjt.emp.config;

import cn.dev33.satoken.stp.StpInterface;
import com.ldjt.emp.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sa-Token权限认证接口实现
 * 
 * @author emp
 */
@Component
@Slf4j
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        try {
            Long userId = Long.valueOf(loginId.toString());
            return sysUserService.getUserPermissions(userId);
        } catch (Exception e) {
            log.error("获取用户权限失败，userId: {}", loginId, e);
            return List.of();
        }
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            Long userId = Long.valueOf(loginId.toString());
            return sysUserService.getUserRoles(userId);
        } catch (Exception e) {
            log.error("获取用户角色失败，userId: {}", loginId, e);
            return List.of();
        }
    }
}
