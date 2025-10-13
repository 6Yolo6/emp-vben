package com.ldjt.emp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API文档配置
 * 
 * @author EMP Team
 */
@Configuration
public class Knife4jConfig {

    /**
     * OpenAPI 配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(components())
                .addSecurityItem(securityRequirement());
    }

    /**
     * API 信息
     */
    private Info apiInfo() {
        return new Info()
                .title("EMP 企业综合管理平台 API")
                .description("企业综合管理平台接口文档")
                .version("1.0.0")
                .contact(new Contact()
                        .name("EMP Team")
                        .email("support@emp.com")
                        .url("https://emp.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }

    /**
     * 安全组件配置
     */
    private Components components() {
        return new Components()
                .addSecuritySchemes("Authorization", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .description("请输入 Token，格式：Bearer {token}"));
    }

    /**
     * 安全要求
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("Authorization");
    }
}
