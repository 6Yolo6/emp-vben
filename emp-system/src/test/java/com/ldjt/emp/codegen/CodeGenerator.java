package com.ldjt.emp.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
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
        globalConfig.setBasePackage("com.ldjt.emp");

        // 设置表前缀，生成实体类时会去掉前缀
        globalConfig.setTablePrefix("sys_", "wf_");

        // 设置生成路径
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/emp-system/src/main/java");
        globalConfig.setMapperXmlPath(System.getProperty("user.dir") + "/emp-system/src/main/resources/mapper");

        // Entity配置
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setEntityWithSwagger(true);
        globalConfig.setEntitySuperClass(com.ldjt.emp.common.core.domain.BaseEntity.class);

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

        // 设置作者
        globalConfig.setAuthor("emp");

        // 设置生成策略
        globalConfig.setGenerateForView(false);

        return globalConfig;
    }
}
