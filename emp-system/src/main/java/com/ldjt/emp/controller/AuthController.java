package com.ldjt.emp.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证测试Controller
 *
 * @author wdf
 * @since 2025/9/29 10:00
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 测试登录接口
     */
    @PostMapping("/login")
    public SaResult login(@RequestParam String username, @RequestParam String password) {
        // 简单的用户验证（实际项目中应该查询数据库）
        if ("admin".equals(username) && "123456".equals(password)) {
            // 登录成功，生成token
            StpUtil.login(10001);  // 用户ID
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", StpUtil.getTokenValue());
            data.put("userId", 10001);
            data.put("username", username);
            
            return SaResult.ok("登录成功").setData(data);
        } else {
            return SaResult.error("用户名或密码错误");
        }
    }

    /**
     * 测试登出接口
     */
    @PostMapping("/logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok("登出成功");
    }

    /**
     * 测试获取登录信息
     */
    @GetMapping("/info")
    public SaResult getLoginInfo() {
        // 检查是否登录
        if (!StpUtil.isLogin()) {
            return SaResult.error("未登录");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", StpUtil.getLoginId());
        data.put("token", StpUtil.getTokenValue());
        data.put("tokenInfo", StpUtil.getTokenInfo());
        
        return SaResult.ok("获取成功").setData(data);
    }

    /**
     * 测试需要登录才能访问的接口
     */
    @GetMapping("/userinfo")
    public SaResult getUserInfo() {
        // 检查登录状态，未登录会抛出异常
        StpUtil.checkLogin();
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", StpUtil.getLoginId());
        userInfo.put("username", "admin");
        userInfo.put("roles", new String[]{"admin", "user"});
        
        return SaResult.ok("获取用户信息成功").setData(userInfo);
    }

    /**
     * 测试权限验证
     */
    @GetMapping("/admin")
    public SaResult adminOnly() {
        // 检查是否有admin权限（这里简化处理）
        StpUtil.checkLogin();
        
        if (StpUtil.getLoginId().equals(10001)) {
            return SaResult.ok("管理员专属接口访问成功");
        } else {
            return SaResult.error("权限不足");
        }
    }
}
