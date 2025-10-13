package com.ldjt.emp.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.PackageConfig;
import com.mybatisflex.codegen.config.StrategyConfig;
import com.mybatisflex.codegen.config.TemplateConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 自定义代码生成器
 * 支持更灵活的配置
 * 
 * @author emp
 */
public class CustomCodeGenerator {

    public static void main(String[] args) {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/emp_dev");
        dataSource.setUsername("postgres");
        dataSource.setPassword("Root@123");

        // 创建全局配置
        GlobalConfig globalConfig = createGlobalConfig();

        // 创建包配置
        PackageConfig packageConfig = createPackageConfig();

        // 创建策略配置
        StrategyConfig strategyConfig = createStrategyConfig();

        // 创建模板配置
        TemplateConfig templateConfig = createTemplateConfig();

        // 应用配置
        globalConfig.setPackageConfig(packageConfig);
        globalConfig.setStrategyConfig(strategyConfig);
        globalConfig.setTemplateConfig(templateConfig);

        // 创建生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 生成代码
        generator.generate();
    }

    /**
     * 创建全局配置
     */
    private static GlobalConfig createGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();

        // 基础配置
        globalConfig.setAuthor("emp");
        globalConfig.setBasePackage("com.ldjt.emp");

        // 生成路径配置
        String projectPath = System.getProperty("user.dir");
        globalConfig.setSourceDir(projectPath + "/emp-system/src/main/java");
        globalConfig.setMapperXmlPath(projectPath + "/emp-system/src/main/resources/mapper");

        // Entity配置
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntityWithSwagger(true);
        globalConfig.setEntitySuperClass(com.ldjt.emp.common.core.domain.BaseEntity.class);
        globalConfig.setEntityJdkVersion(17);

        // Mapper配置
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.setMapperAnnotation(true);

        // Service配置
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);

        // Controller配置
        globalConfig.setControllerGenerateEnable(true);
        globalConfig.setControllerRestStyle(true);

        // TableDef配置
        globalConfig.setTableDefGenerateEnable(true);

        // MapperXml配置
        globalConfig.setMapperXmlGenerateEnable(true);

        return globalConfig;
    }

    /**
     * 创建包配置
     */
    private static PackageConfig createPackageConfig() {
        PackageConfig packageConfig = new PackageConfig();

        // 设置各层包名
        packageConfig.setEntity("entity");
        packageConfig.setMapper("mapper");
        packageConfig.setService("service");
        packageConfig.setServiceImpl("service.impl");
        packageConfig.setController("controller");
        packageConfig.setTableDef("entity.table");

        return packageConfig;
    }

    /**
     * 创建策略配置
     */
    private static StrategyConfig createStrategyConfig() {
        StrategyConfig strategyConfig = new StrategyConfig();

        // 设置需要生成的表（可以使用通配符）
        // strategyConfig.setGenerateTable("sys_user", "sys_role", "sys_menu");

        // 设置表前缀
        strategyConfig.setTablePrefix("sys_", "wf_");

        // 设置需要忽略的字段（BaseEntity中已有）
        strategyConfig.setIgnoreColumns(
            "create_by",
            "create_time",
            "update_by",
            "update_time"
        );

        // 设置逻辑删除字段
        strategyConfig.setLogicDeleteColumn("deleted");

        // 设置版本字段（乐观锁）
        // strategyConfig.setVersionColumn("version");

        return strategyConfig;
    }

    /**
     * 创建模板配置
     */
    private static TemplateConfig createTemplateConfig() {
        TemplateConfig templateConfig = new TemplateConfig();

        // 使用自定义模板（如果需要）
        // templateConfig.setEntity("/templates/entity.java");
        // templateConfig.setMapper("/templates/mapper.java");
        // templateConfig.setService("/templates/service.java");
        // templateConfig.setServiceImpl("/templates/serviceImpl.java");
        // templateConfig.setController("/templates/controller.java");

        return templateConfig;
    }
}
