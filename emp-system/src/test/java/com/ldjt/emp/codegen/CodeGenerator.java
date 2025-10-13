package com.ldjt.emp.codegen;

import com.ldjt.emp.common.core.domain.BaseEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * MyBatis-Flex代码生成器
 *
 * @author emp
 */
public class CodeGenerator {

    public static void main(String[] args) {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/emp_dev");
        dataSource.setUsername("postgres");
        dataSource.setPassword("Root@123");

        // 创建配置
        GlobalConfig globalConfig = createGlobalConfig();

        // 创建生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 生成代码
        generator.generate();
    }

    /**
     * 创建全局配置
     */
    public static GlobalConfig createGlobalConfig() {
        // 创建配置
        GlobalConfig globalConfig = new GlobalConfig();

        // 设置根包
        globalConfig.getPackageConfig().setBasePackage("com.ldjt.emp");

        // 设置表前缀，生成实体类时会去掉前缀
        globalConfig.getStrategyConfig().setTablePrefix("sys_", "wf_");

        // 忽略BaseEntity中的字段
        globalConfig.getStrategyConfig().setIgnoreColumns(
            "create_by",
            "create_time",
            "update_by",
            "update_time",
            "deleted"
        );

        // 设置生成路径
        globalConfig.getJavadocConfig().setAuthor("emp");
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/emp-system/src/main/java");
        globalConfig.setMapperXmlPath(System.getProperty("user.dir") + "/emp-system/src/main/resources/mapper");

        // Entity配置
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntityWithSwagger(false);  // 暂时禁用Swagger注解，避免生成旧版注解
        globalConfig.setEntitySuperClass(BaseEntity.class);

        // Mapper配置
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.getMapperConfig().setMapperAnnotation(true);

        // Service配置
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);

        // Controller配置
        globalConfig.setControllerGenerateEnable(true);
        globalConfig.getControllerConfig().setRestStyle(true);

        // TableDef配置
        globalConfig.setTableDefGenerateEnable(true);

        // MapperXml配置
        globalConfig.setMapperXmlGenerateEnable(true);

        // 设置生成策略
        globalConfig.getStrategyConfig().setGenerateForView(false);

        return globalConfig;
    }
}
