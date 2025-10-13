package com.ldjt.emp.config;


import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Druid监控配置
 *
 * @author emp
 */
@Configuration
public class DruidConfig {

    /**
     * 配置Druid监控页面
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");

        Map<String, String> initParams = new HashMap<>();
        // 监控页面登录用户名和密码
        initParams.put("loginUsername", "admin");
        initParams.put("loginPassword", "admin123");
        // 默认允许所有访问
        initParams.put("allow", "");
        // 拒绝访问的IP（黑名单）
        // initParams.put("deny", "192.168.1.100");

        bean.setInitParameters(initParams);
        return bean;
    }

    /**
     * 配置Web监控的Filter
     */
    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());

        Map<String, String> initParams = new HashMap<>();
        // 不统计这些请求数据
        initParams.put("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");

        bean.setInitParameters(initParams);
        // 设置拦截请求
        bean.setUrlPatterns(Arrays.asList("/*"));

        return bean;
    }
}
