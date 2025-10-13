package com.ldjt.emp.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.StrategyConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 生成系统管理模块代码
 * 包括：用户、角色、菜单、部门管理
 * 
 * @author emp
 */
public class GenerateSystemModule {

    public static void main(String[] args) {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/emp_dev");
        dataSource.setUsername("postgres");
        dataSource.setPassword("Root@123");

        // 创建全局配置
        GlobalConfig globalConfig = createGlobalConfig();

        // 创建策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        
        // 指定要生成的表
        strategyConfig.setGenerateTable(
            "sys_user",    // 用户表
            "sys_role",    // 角色表
            "sys_menu",    // 菜单表
            "sys_dept",    // 部门表
            "sys_post"     // 岗位表
        );
        
        // 设置表前缀
        strategyConfig.setTablePrefix("sys_");
        
        // 忽略BaseEntity中的字段
        strategyConfig.setIgnoreColumns(
            "create_by",
            "create_time",
            "update_by",
            "update_time"
        );
        
        // 设置逻辑删除字段
        strategyConfig.setLogicDeleteColumn("deleted");
        
        globalConfig.setStrategyConfig(strategyConfig);

        // 创建生成器并生成代码
        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
        
        System.out.println("代码生成完成！");
        System.out.println("生成的文件位置：");
        System.out.println("  - Entity: emp-system/src/main/java/com/ldjt/emp/entity/");
        System.out.println("  - Mapper: emp-system/src/main/java/com/ldjt/emp/mapper/");
        System.out.println("  - Service: emp-system/src/main/java/com/ldjt/emp/service/");
        System.out.println("  - Controller: emp-system/src/main/java/com/ldjt/emp/controller/");
        System.out.println("  - MapperXml: emp-system/src/main/resources/mapper/");
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

        // 配置需要忽略的字段（BaseEntity中已有）
        globalConfig.setColumnConfig("create_by", new ColumnConfig().setIgnore(true));
        globalConfig.setColumnConfig("create_time", new ColumnConfig().setIgnore(true));
        globalConfig.setColumnConfig("update_by", new ColumnConfig().setIgnore(true));
        globalConfig.setColumnConfig("update_time", new ColumnConfig().setIgnore(true));

        return globalConfig;
    }
}
