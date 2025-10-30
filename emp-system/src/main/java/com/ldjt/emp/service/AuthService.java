package com.ldjt.emp.service;

import com.ldjt.emp.dto.LoginRequest;
import com.ldjt.emp.dto.LoginResponse;

/**
 * 认证服务接口
 * 
 * @author emp
 */
public interface AuthService {

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @param loginIp 登录IP
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest, String loginIp);

    /**
     * 用户登出
     * 
     * @return 是否成功
     */
    boolean logout();

    /**
     * 获取当前登录用户信息
     * 
     * @return 用户信息
     */
    LoginResponse.UserInfo getCurrentUserInfo();

    /**
     * 刷新Token
     * 
     * @return Token信息
     */
    LoginResponse.TokenInfo refreshToken();

    /**
     * 切换租户（通过租户编码）
     * 
     * @param tenantCode 租户编码
     * @return 用户信息
     */
    LoginResponse.UserInfo switchTenant(String tenantCode);
    
    /**
     * 切换租户（通过租户ID）
     * 
     * @param tenantId 租户ID
     * @return 用户信息
     */
    LoginResponse.UserInfo switchTenantById(Long tenantId);

    /**
     * 获取用户可用的租户列表
     * 
     * @param username 用户名
     * @return 租户列表
     */
    java.util.List<com.ldjt.emp.vo.tenant.TenantVO> getUserAvailableTenants(String username);

    /**
     * 获取所有可用的租户列表
     * 
     * @return 租户列表
     */
    java.util.List<com.ldjt.emp.vo.tenant.TenantVO> getAllAvailableTenants();
    
    /**
     * 管理员切换到其他用户（用于调试）
     * 
     * @param targetUserId 目标用户ID
     * @param tenantId 目标租户ID（可选，不指定则使用主租户）
     * @return 登录响应（包含新token）
     */
    LoginResponse switchToUser(Long targetUserId, Long tenantId);
    
    /**
     * 切换回原账号
     * 
     * @return 登录响应（包含新token）
     */
    LoginResponse switchBackToOriginal();
}
