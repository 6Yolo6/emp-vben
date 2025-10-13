package com.ldjt.emp.service.impl;

import com.ldjt.emp.entity.SysUser;
import com.ldjt.emp.mapper.SysUserMapper;
import com.ldjt.emp.service.SysUserService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ldjt.emp.entity.table.SysUserTableDef.SYS_USER;

/**
 * 用户服务实现类
 * 
 * @author emp
 */
@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SysUser getUserByUsername(String username) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER.USERNAME.eq(username))
                .and(SYS_USER.DELETED.eq(0));
        return sysUserMapper.selectOneByQuery(queryWrapper);
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public void updateLoginInfo(Long userId, String loginIp) {
        sysUserMapper.updateLoginInfo(userId, loginIp, LocalDateTime.now());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        // TODO: 实现用户权限查询逻辑
        // 这里先返回空列表，后续在权限模块中实现
        List<String> permissions = new ArrayList<>();
        
        // 超级管理员拥有所有权限
        SysUser user = getUserById(userId);
        if (user != null && "admin".equals(user.getUsername())) {
            permissions.add("*:*:*"); // 超级权限
        }
        
        return permissions;
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        // TODO: 实现用户角色查询逻辑
        // 这里先返回空列表，后续在权限模块中实现
        List<String> roles = new ArrayList<>();
        
        // 超级管理员角色
        SysUser user = getUserById(userId);
        if (user != null && "admin".equals(user.getUsername())) {
            roles.add("admin");
        }
        
        return roles;
    }

    @Override
    public SysUser getUserById(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_USER.ID.eq(userId))
                .and(SYS_USER.DELETED.eq(0));
        return sysUserMapper.selectOneByQuery(queryWrapper);
    }
}
