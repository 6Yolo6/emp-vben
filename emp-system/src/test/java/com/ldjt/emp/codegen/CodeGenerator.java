package com.ldjt.emp.codegen;

import com.ldjt.emp.common.core.domain.BaseEntity;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.TemplateConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * MyBatis-Flex代码生成器
 *
 * @author emp
 */
public class CodeGenerator {

    public static void main(String[] args) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/emp_dev");
        dataSource.setUsername("postgres");
        dataSource.setPassword("Root@123");

        GlobalConfig globalConfig = createGlobalConfig();
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }

    public static GlobalConfig createGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();

        // 包配置
        globalConfig.getPackageConfig()
                .setBasePackage("com.ldjt.emp");

        // 策略配置
        globalConfig.getStrategyConfig()
                .setTablePrefix("sys_", "wf_")
                .setGenerateTable("sys_user", "sys_role", "sys_menu", "sys_dept", "sys_post")
                // ⭐ 暂时不忽略列，让它正常生成
                .setGenerateForView(false);

        // 作者配置
        globalConfig.getJavadocConfig()
                .setAuthor("emp")
                .setSince("1.0.0");

        // 路径配置
        String projectPath = System.getProperty("user.dir");
        globalConfig.setSourceDir(projectPath + "/emp-system/src/main/java");
        globalConfig.setMapperXmlPath(projectPath + "/emp-system/src/main/resources/mapper");

        // Entity配置 - ⭐ 暂时不设置父类
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        // globalConfig.setEntitySuperClass(BaseEntity.class); // 暂时注释
        globalConfig.getEntityConfig()
                .setWithSwagger(true);

        // Mapper配置
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.getMapperConfig()
                .setMapperAnnotation(true);

        // Service配置
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);

        // Controller配置
        globalConfig.setControllerGenerateEnable(true);
        globalConfig.getControllerConfig()
                .setRestStyle(true);

        // TableDef配置
        globalConfig.setTableDefGenerateEnable(true);

        // MapperXml配置
        globalConfig.setMapperXmlGenerateEnable(true);

        return globalConfig;
    }
}
