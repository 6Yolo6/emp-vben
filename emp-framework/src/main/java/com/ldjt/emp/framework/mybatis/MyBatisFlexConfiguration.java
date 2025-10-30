package com.ldjt.emp.framework.mybatis;

import com.ldjt.emp.framework.tenant.TenantLineHandler;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Flex配置
 * 
 * @author emp
 */
@Slf4j
@Configuration
public class MyBatisFlexConfiguration {

    /**
     * 配置MyBatis-Flex全局配置
     */
    @Bean
    public MyBatisFlexCustomizer myBatisFlexCustomizer(AuditFieldHandler auditFieldHandler,
                                                       TenantLineHandler tenantLineHandler) {
        return configurer -> {
            FlexGlobalConfig globalConfig = FlexGlobalConfig.getDefaultConfig();

            // 注册审计字段自动填充监听器
            globalConfig.registerInsertListener(auditFieldHandler, BaseEntity.class);
            globalConfig.registerUpdateListener(auditFieldHandler, BaseEntity.class);

            // 配置租户插件
            TenantManager.setTenantFactory(tenantLineHandler);
            log.info("MyBatis-Flex 租户插件配置完成");

            // 开启SQL审计（开发环境）
            if (isDevEnvironment()) {
                AuditManager.setAuditEnable(true);
                MessageCollector collector = new ConsoleMessageCollector();
                AuditManager.setMessageCollector(collector);
            }
        };
    }

    /**
     * 判断是否为开发环境
     */
    private boolean isDevEnvironment() {
        String env = System.getProperty("spring.profiles.active", "dev");
        return "dev".equals(env) || "local".equals(env);
    }
}
