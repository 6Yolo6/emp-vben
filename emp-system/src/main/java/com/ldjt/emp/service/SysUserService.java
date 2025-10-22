package com.ldjt.emp.service;

import com.ldjt.emp.dto.UserPageQueryDTO;
import com.ldjt.emp.entity.SysUser;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author emp
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);

    /**
     * 验证用户密码
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    boolean checkPassword(String rawPassword, String encodedPassword);

    /**
     * 更新用户登录信息
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP
     */
    void updateLoginInfo(Long userId, String loginIp);

    /**
     * 获取用户权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 获取用户角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 根据用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getUserById(Long userId);
    
    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return 是否成功
     */
    boolean createUser(SysUser user);
    
    /**
     * 更新用户
     * 
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(SysUser user);
    
    /**
     * 删除用户（逻辑删除）
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId);
    
    /**
     * 更新用户状态
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateUserStatus(Long userId, Integer status);
    
    /**
     * 重置用户密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(Long userId, String newPassword);
    
    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    List<SysUser> listAllUsers();
    
    /**
     * 分页查询用户
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<SysUser> pageQuery(UserPageQueryDTO queryDTO);
    
    /**
     * 分配用户角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);
    
    /**
     * 获取用户的角色ID列表
     * 
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);
    
    /**
     * 批量删除用户
     * 
     * @param userIds 用户ID列表
     * @return 是否成功
     */
    boolean batchDelete(List<Long> userIds);
    
    /**
     * 分配用户岗位
     * 
     * @param userId 用户ID
     * @param postIds 岗位ID列表
     * @return 是否成功
     */
    boolean assignPosts(Long userId, List<Long> postIds);
    
    /**
     * 获取用户的岗位ID列表
     * 
     * @param userId 用户ID
     * @return 岗位ID列表
     */
    List<Long> getUserPostIds(Long userId);
}
