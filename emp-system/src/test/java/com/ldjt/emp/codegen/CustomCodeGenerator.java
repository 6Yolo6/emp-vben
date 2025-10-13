package com.ldjt.emp.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.PackageConfig;
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
        globalConfig.getJavadocConfig().setAuthor("emp");
        globalConfig.getPackageConfig().setBasePackage("com.ldjt.emp");

        // 生成路径配置
        String projectPath = System.getProperty("user.dir");
        globalConfig.setSourceDir(projectPath + "/emp-system/src/main/java");
        globalConfig.setMapperXmlPath(projectPath + "/emp-system/src/main/resources/mapper");

        // 包配置
        PackageConfig packageConfig = globalConfig.getPackageConfig();
        packageConfig.setEntityPackage("entity"); // 实体类包路径（相对于basePackage）
        packageConfig.setMapperPackage("mapper"); // Mapper接口包路径
        packageConfig.setServicePackage("service"); // Service接口包路径
        packageConfig.setServiceImplPackage("service.impl"); // Service实现类包路径
        packageConfig.setControllerPackage("controller"); // Controller包路径
        packageConfig.setTableDefPackage("entity.table"); // 表定义类包路径

        // Entity配置 - 使用OpenAPI 3.0注解
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntityWithSwagger(false);  // 暂时禁用Swagger注解，避免生成旧版注解
//        globalConfig.getEntityConfig().setWithSwagger(true);
//        globalConfig.getEntityConfig().setSwaggerVersion(GlobalConfig.SwaggerVersion.FOX);
        globalConfig.setEntitySuperClass(com.ldjt.emp.common.core.domain.BaseEntity.class);

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

        // 策略配置
        // 设置需要生成的表（可以使用通配符）
        // globalConfig.getStrategyConfig().setGenerateTable("sys_user", "sys_role", "sys_menu");

        // 设置表前缀
        globalConfig.getStrategyConfig().setTablePrefix("sys_", "wf_");

        // 设置需要忽略的字段（BaseEntity中已有）
        globalConfig.getStrategyConfig().setIgnoreColumns(
            "create_by",
            "create_time",
            "update_by",
            "update_time",
            "deleted"
        );

        // 设置逻辑删除字段
        globalConfig.getStrategyConfig().setLogicDeleteColumn("deleted");

        // 设置版本字段（乐观锁）
        // globalConfig.getStrategyConfig().setVersionColumn("version");

        // 模板配置
        // 使用自定义模板（如果需要）
        // globalConfig.getTemplateConfig().setEntity("/templates/entity.java");
        // globalConfig.getTemplateConfig().setMapper("/templates/mapper.java");
        // globalConfig.getTemplateConfig().setService("/templates/service.java");
        // globalConfig.getTemplateConfig().setServiceImpl("/templates/serviceImpl.java");
        // globalConfig.getTemplateConfig().setController("/templates/controller.java");

        return globalConfig;
    }
}
