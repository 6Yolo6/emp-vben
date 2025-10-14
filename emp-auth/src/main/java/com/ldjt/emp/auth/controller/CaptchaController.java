package com.ldjt.emp.auth.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.ldjt.emp.auth.service.CaptchaService;
import com.ldjt.emp.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码控制器
 * 只提供验证码服务，登录/登出功能在system模块
 *
 * @author emp
 */
@Tag(name = "验证码管理")
@RestController
@RequestMapping("/auth")
@Slf4j
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取验证码
     */
    @SaIgnore
    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result<CaptchaService.CaptchaVO> getCaptcha() {
        return Result.success(captchaService.generateCaptcha());
    }
}
