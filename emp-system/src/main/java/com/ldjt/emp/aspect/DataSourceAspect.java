package com.ldjt.emp.aspect;

import com.ldjt.emp.annotation.DataSource;
import com.ldjt.emp.config.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据源切换切面
 * 
 * @author emp
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class DataSourceAspect {
    
    @Pointcut("@annotation(com.ldjt.emp.annotation.DataSource) || @within(com.ldjt.emp.annotation.DataSource)")
    public void dataSourcePointCut() {
    }
    
    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSource dataSource = getDataSource(point);
        
        if (dataSource != null) {
            DataSourceContextHolder.setDataSourceType(dataSource.value());
        }
        
        try {
            return point.proceed();
        } finally {
            // 销毁数据源，在执行方法之后
            DataSourceContextHolder.clearDataSourceType();
        }
    }
    
    /**
     * 获取需要切换的数据源
     */
    public DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        
        DataSource dataSource = AnnotationUtils.findAnnotation(method, DataSource.class);
        if (dataSource != null) {
            return dataSource;
        }
        
        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class);
    }
}
