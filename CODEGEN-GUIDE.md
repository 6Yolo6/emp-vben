# MyBatis-Flex 代码生成器使用指南

## 概述

MyBatis-Flex代码生成器可以根据数据库表结构自动生成Entity、Mapper、Service、Controller等代码，大大提高开发效率。

## 生成器类型

### 1. CodeGenerator（基础版）
**文件：** `emp-system/src/test/java/com/ldjt/emp/codegen/CodeGenerator.java`

**特点：**
- 简单易用，配置少
- 适合快速生成基础代码
- 使用默认模板

**使用方法：**
```java
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
```

### 2. CustomCodeGenerator（自定义版）
**文件：** `emp-system/src/test/java/com/ldjt/emp/codegen/CustomCodeGenerator.java`

**特点：**
- 配置灵活，可定制性强
- 支持自定义模板
- 支持策略配置

**使用方法：**
```java
public static void main(String[] args) {
    // 配置数据源
    HikariDataSource dataSource = new HikariDataSource();
    // ...

    // 创建各种配置
    GlobalConfig globalConfig = createGlobalConfig();
    PackageConfig packageConfig = createPackageConfig();
    StrategyConfig strategyConfig = createStrategyConfig();
    TemplateConfig templateConfig = createTemplateConfig();

    // 应用配置
    globalConfig.setPackageConfig(packageConfig);
    globalConfig.setStrategyConfig(strategyConfig);
    globalConfig.setTemplateConfig(templateConfig);

    // 生成代码
    Generator generator = new Generator(dataSource, globalConfig);
    generator.generate();
}
```

## 配置说明

### 全局配置（GlobalConfig）

```java
GlobalConfig globalConfig = new GlobalConfig();

// 基础配置
globalConfig.setAuthor("emp");                    // 作者
globalConfig.setBasePackage("com.ldjt.emp");      // 基础包名

// 生成路径
globalConfig.setSourceDir(projectPath + "/emp-system/src/main/java");
globalConfig.setMapperXmlPath(projectPath + "/emp-system/src/main/resources/mapper");

// Entity配置
globalConfig.setEntityGenerateEnable(true);       // 是否生成Entity
globalConfig.setEntityWithLombok(true);           // 使用Lombok
globalConfig.setEntityWithSwagger(true);          // 使用Swagger注解
globalConfig.setEntitySuperClass(BaseEntity.class); // 继承BaseEntity

// Mapper配置
globalConfig.setMapperGenerateEnable(true);       // 是否生成Mapper
globalConfig.setMapperAnnotation(true);           // 使用@Mapper注解

// Service配置
globalConfig.setServiceGenerateEnable(true);      // 是否生成Service
globalConfig.setServiceImplGenerateEnable(true);  // 是否生成ServiceImpl

// Controller配置
globalConfig.setControllerGenerateEnable(true);   // 是否生成Controller
globalConfig.setControllerRestStyle(true);        // 使用RESTful风格
```

### 包配置（PackageConfig）

```java
PackageConfig packageConfig = new PackageConfig();

packageConfig.setEntity("entity");                // Entity包名
packageConfig.setMapper("mapper");                // Mapper包名
packageConfig.setService("service");              // Service包名
packageConfig.setServiceImpl("service.impl");     // ServiceImpl包名
packageConfig.setController("controller");        // Controller包名
packageConfig.setTableDef("entity.table");        // TableDef包名
```

### 策略配置（StrategyConfig）

```java
StrategyConfig strategyConfig = new StrategyConfig();

// 指定要生成的表
strategyConfig.setGenerateTable("sys_user", "sys_role", "sys_menu");

// 设置表前缀（生成类名时会去掉前缀）
strategyConfig.setTablePrefix("sys_", "wf_");

// 忽略字段（BaseEntity中已有的字段）
strategyConfig.setIgnoreColumns(
    "create_by",
    "create_time",
    "update_by",
    "update_time"
);

// 逻辑删除字段
strategyConfig.setLogicDeleteColumn("deleted");

// 乐观锁字段
strategyConfig.setVersionColumn("version");
```

### 模板配置（TemplateConfig）

```java
TemplateConfig templateConfig = new TemplateConfig();

// 使用自定义模板
templateConfig.setEntity("/templates/entity.java.vm");
templateConfig.setMapper("/templates/mapper.java.vm");
templateConfig.setService("/templates/service.java.vm");
templateConfig.setServiceImpl("/templates/serviceImpl.java.vm");
templateConfig.setController("/templates/controller.java.vm");
```

## 自定义模板

### 模板位置
`emp-system/src/test/resources/templates/`

### 可用模板
- `entity.java.vm` - Entity实体类模板
- `controller.java.vm` - Controller控制器模板
- `mapper.java.vm` - Mapper接口模板
- `service.java.vm` - Service接口模板
- `serviceImpl.java.vm` - ServiceImpl实现类模板

### 模板变量

**表信息：**
- `${table.name}` - 表名
- `${table.comment}` - 表注释
- `${entity}` - 实体类名
- `${controllerName}` - Controller类名

**列信息：**
- `${column.name}` - 列名
- `${column.comment}` - 列注释
- `${column.property}` - 属性名
- `${column.propertySimpleType}` - 属性类型
- `${column.primaryKey}` - 是否主键
- `${column.logicDelete}` - 是否逻辑删除字段

**包信息：**
- `${packageConfig.entityPackage}` - Entity包名
- `${packageConfig.mapperPackage}` - Mapper包名
- `${packageConfig.servicePackage}` - Service包名
- `${packageConfig.controllerPackage}` - Controller包名

## 使用步骤

### 1. 准备数据库表

确保数据库中已经创建了需要生成代码的表。

```sql
-- 示例：sys_dept表
CREATE TABLE sys_dept (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT,
    dept_name VARCHAR(50) NOT NULL,
    order_num INT DEFAULT 0,
    leader VARCHAR(20),
    phone VARCHAR(11),
    email VARCHAR(50),
    status SMALLINT DEFAULT 1,
    deleted SMALLINT DEFAULT 0,
    create_by VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.id IS '部门ID';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
```

### 2. 配置数据源

修改生成器中的数据源配置：

```java
HikariDataSource dataSource = new HikariDataSource();
dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/emp_dev");
dataSource.setUsername("postgres");
dataSource.setPassword("Root@123");
```

### 3. 配置生成策略

指定要生成的表：

```java
// 方式1：生成所有表
generator.generate();

// 方式2：生成指定表
strategyConfig.setGenerateTable("sys_dept", "sys_post");
```

### 4. 运行生成器

在IDE中运行生成器的main方法：
- 右键点击 `CodeGenerator.java`
- 选择 `Run 'CodeGenerator.main()'`

### 5. 检查生成的代码

生成的代码位置：
```
emp-system/src/main/java/com/ldjt/emp/
├── entity/
│   └── SysDept.java
├── mapper/
│   └── SysDeptMapper.java
├── service/
│   ├── SysDeptService.java
│   └── impl/
│       └── SysDeptServiceImpl.java
└── controller/
    └── SysDeptController.java

emp-system/src/main/resources/mapper/
└── SysDeptMapper.xml
```

### 6. 验证生成的代码

#### 6.1 检查Entity
```java
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_dept")
@Schema(description = "部门表")
public class SysDept extends BaseEntity {
    @Id(keyType = KeyType.Auto)
    @Schema(description = "部门ID")
    private Long id;

    @Schema(description = "部门名称")
    private String deptName;
    
    // ... 其他字段
}
```

#### 6.2 检查Mapper
```java
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
    // 继承BaseMapper，拥有基础CRUD方法
}
```

#### 6.3 检查Service
```java
public interface SysDeptService extends IService<SysDept> {
    // 继承IService，拥有基础CRUD方法
}

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> 
        implements SysDeptService {
    // 实现类
}
```

#### 6.4 检查Controller
```java
@RestController
@RequestMapping("/sysDept")
@Tag(name = "部门表管理")
public class SysDeptController {
    @Autowired
    private SysDeptService sysDeptService;

    @GetMapping("/page")
    public Result<Page<SysDept>> page(...) {
        // 分页查询
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody SysDept entity) {
        // 新增
    }
    
    // ... 其他方法
}
```

### 7. 编译测试

```bash
cd emp-system
mvn clean compile
```

## 生成示例

### 示例1：生成部门管理代码

```java
public static void main(String[] args) {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/emp_dev");
    dataSource.setUsername("postgres");
    dataSource.setPassword("Root@123");

    GlobalConfig globalConfig = new GlobalConfig();
    globalConfig.setBasePackage("com.ldjt.emp");
    globalConfig.setTablePrefix("sys_");
    
    // 只生成sys_dept表
    StrategyConfig strategyConfig = new StrategyConfig();
    strategyConfig.setGenerateTable("sys_dept");
    globalConfig.setStrategyConfig(strategyConfig);

    Generator generator = new Generator(dataSource, globalConfig);
    generator.generate();
}
```

### 示例2：批量生成系统管理模块

```java
public static void main(String[] args) {
    // ... 数据源配置

    StrategyConfig strategyConfig = new StrategyConfig();
    strategyConfig.setGenerateTable(
        "sys_dept",    // 部门表
        "sys_post",    // 岗位表
        "sys_dict_type", // 字典类型表
        "sys_dict_data"  // 字典数据表
    );
    
    globalConfig.setStrategyConfig(strategyConfig);
    
    Generator generator = new Generator(dataSource, globalConfig);
    generator.generate();
}
```

## 注意事项

### 1. 数据库连接
- 确保数据库服务正在运行
- 确保数据库连接信息正确
- 确保数据库用户有读取表结构的权限

### 2. 表设计规范
- 表名使用小写+下划线命名（如：sys_user）
- 字段名使用小写+下划线命名（如：user_name）
- 主键字段统一命名为id
- 必须有表注释和字段注释

### 3. 生成代码检查
- 检查生成的代码是否符合项目规范
- 检查import语句是否正确
- 检查继承关系是否正确
- 检查注解是否完整

### 4. 代码覆盖
- 生成器会覆盖已存在的文件
- 建议先备份已修改的代码
- 或者使用版本控制系统

### 5. 手动调整
生成的代码可能需要手动调整：
- 添加业务方法
- 添加复杂查询
- 添加数据验证
- 添加权限控制

## 常见问题

### Q1: 生成的代码编译报错？
**A:** 检查以下几点：
1. 确保BaseEntity类存在
2. 确保依赖已正确导入
3. 确保包名配置正确
4. 运行 `mvn clean compile` 重新编译

### Q2: 如何只生成部分文件？
**A:** 在GlobalConfig中设置：
```java
globalConfig.setEntityGenerateEnable(true);      // 生成Entity
globalConfig.setMapperGenerateEnable(false);     // 不生成Mapper
globalConfig.setServiceGenerateEnable(false);    // 不生成Service
globalConfig.setControllerGenerateEnable(false); // 不生成Controller
```

### Q3: 如何自定义生成的代码？
**A:** 创建自定义模板：
1. 在 `src/test/resources/templates/` 创建模板文件
2. 使用Velocity语法编写模板
3. 在TemplateConfig中指定模板路径

### Q4: 生成的字段类型不对？
**A:** MyBatis-Flex会自动映射数据库类型到Java类型：
- BIGINT → Long
- VARCHAR → String
- TIMESTAMP → LocalDateTime
- SMALLINT → Integer

如需自定义，可以在生成后手动修改。

### Q5: 如何处理BaseEntity中的字段？
**A:** 在StrategyConfig中忽略这些字段：
```java
strategyConfig.setIgnoreColumns(
    "create_by",
    "create_time",
    "update_by",
    "update_time"
);
```

## 最佳实践

### 1. 分模块生成
不要一次生成所有表，建议按模块分批生成：
- 系统管理模块：sys_user, sys_role, sys_menu
- 工作流模块：wf_model, wf_process, wf_task
- 业务模块：根据具体业务

### 2. 使用版本控制
生成代码前：
```bash
git add .
git commit -m "生成代码前的备份"
```

生成代码后：
```bash
git diff  # 查看变更
git add .
git commit -m "生成XXX模块代码"
```

### 3. 代码审查
生成代码后进行审查：
- 检查命名是否规范
- 检查注释是否完整
- 检查逻辑是否正确
- 添加必要的业务方法

### 4. 测试验证
```bash
# 编译测试
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run
```

## 相关文档

- [MyBatis-Flex官方文档](https://mybatis-flex.com/)
- [代码生成器文档](https://mybatis-flex.com/zh/intro/codegen.html)
- [Velocity模板语法](https://velocity.apache.org/engine/devel/user-guide.html)

## 总结

MyBatis-Flex代码生成器可以大大提高开发效率，但生成的代码只是基础框架，实际开发中还需要：

1. ✅ 添加业务逻辑
2. ✅ 添加数据验证
3. ✅ 添加权限控制
4. ✅ 添加异常处理
5. ✅ 编写单元测试
6. ✅ 完善API文档

合理使用代码生成器，可以让我们把更多精力放在业务逻辑的实现上！
