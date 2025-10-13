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
}
