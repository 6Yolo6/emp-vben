# EMP Platform 问题修复记录

## 修复 #1: SpringDoc 依赖兼容性问题

### 问题描述

**错误信息**：
```
java.lang.NoClassDefFoundError: org/springframework/web/servlet/resource/LiteWebJarsResourceResolver
```

**原因分析**：
- SpringDoc 2.x 版本依赖 `LiteWebJarsResourceResolver` 类
- 该类是在 Spring Web 6.2（Spring Boot 3.3.x）中新增的
- 当前项目使用 Spring Boot 3.2.9，不包含此类
- 导致应用启动失败

### 解决方案

**移除 SpringDoc，改用 Knife4j**

#### 1. 修改 emp-common/pom.xml

**移除**：
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.4.0</version>
</dependency>
```

**替换为**：
```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
</dependency>
```

#### 2. 修改父 pom.xml

**移除**：
```xml
<springdoc.version>2.8.9</springdoc.version>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```

**保留**：
```xml
<knife4j.version>4.5.0</knife4j.version>

<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>${knife4j.version}</version>
</dependency>
```

#### 3. 创建 Knife4j 配置类

创建 `emp-system/src/main/java/com/ldjt/emp/config/Knife4jConfig.java`：

```java
@Configuration
public class Knife4jConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(components())
                .addSecurityItem(securityRequirement());
    }
    
    // ... 其他配置
}
```

#### 4. 更新 application.yaml

```yaml
knife4j:
  enable: true
  setting:
    language: zh_cn
```

### 验证结果

✅ 应用启动成功
✅ API 文档可访问：http://localhost:8080/doc.html
✅ 所有 API 接口正常显示
✅ 支持在线调试

### Knife4j vs SpringDoc 对比

| 特性 | Knife4j 4.5.0 | SpringDoc 2.x |
|------|--------------|---------------|
| Spring Boot 3.2.x 兼容性 | ✅ 完全兼容 | ❌ 不兼容 |
| Spring Boot 3.3.x 兼容性 | ✅ 兼容 | ✅ 兼容 |
| UI 界面 | 更美观 | 标准 Swagger UI |
| 中文支持 | ✅ 原生支持 | ⚠️ 需要配置 |
| 在线调试 | ✅ 功能丰富 | ✅ 基础功能 |
| 文档导出 | ✅ 支持 | ❌ 不支持 |
| 学习成本 | 低（兼容 OpenAPI 3.0） | 低 |

### 注解使用说明

Knife4j 使用标准的 OpenAPI 3.0 注解，与 SpringDoc 完全兼容：

```java
// Controller 类注解
@Tag(name = "用户管理", description = "用户相关接口")

// 方法注解
@Operation(summary = "查询用户", description = "根据ID查询用户信息")

// 参数注解
@Parameter(description = "用户ID", required = true)

// 实体类注解
@Schema(description = "用户信息")
```

### 相关文件

- ✅ `emp-common/pom.xml` - 移除 SpringDoc 依赖
- ✅ `pom.xml` - 移除 SpringDoc 版本管理
- ✅ `emp-system/src/main/java/com/ldjt/emp/config/Knife4jConfig.java` - 新增配置类
- ✅ `DEPENDENCIES.md` - 新增依赖说明文档

---

## 修复 #2: Nacos 配置导入问题

### 问题描述

**错误信息**：
```
No spring.config.import property has been defined
```

**原因分析**：
- Spring Cloud 2020+ 版本要求显式配置 Nacos 配置导入
- 或者禁用 Nacos 配置中心的导入检查

### 解决方案

#### 1. 创建 bootstrap.yaml

```yaml
spring:
  cloud:
    nacos:
      config:
        enabled: false  # 暂时禁用
        import-check:
          enabled: false  # 禁用导入检查
```

#### 2. 更新 application.yaml

```yaml
spring:
  cloud:
    nacos:
      discovery:
        enabled: false  # 暂时禁用服务发现
      config:
        enabled: false  # 暂时禁用配置中心
```

#### 3. 添加 Bootstrap 依赖

在 `emp-system/pom.xml` 中添加：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

### 验证结果

✅ 应用启动成功（不依赖 Nacos）
✅ 可以在 Docker 环境就绪后启用 Nacos
✅ 配置灵活，支持本地开发

### 相关文件

- ✅ `emp-system/src/main/resources/bootstrap.yaml` - 新增引导配置
- ✅ `emp-system/src/main/resources/application.yaml` - 更新 Nacos 配置
- ✅ `emp-system/pom.xml` - 添加 Bootstrap 依赖

---

## 修复 #3: 数据库和 Redis 连接信息

### 问题描述

配置文件中的数据库和 Redis 连接信息与 Docker 环境不一致。

### 解决方案

#### 更新 application.yaml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/emp_dev
    username: emp          # 修改为 emp
    password: emp123456    # 修改为 emp123456
    
  data:
    redis:
      password: emp123456  # 添加密码
```

### 验证结果

✅ 数据库连接信息与 Docker 环境一致
✅ Redis 连接信息与 Docker 环境一致

### 相关文件

- ✅ `emp-system/src/main/resources/application.yaml` - 更新连接信息
- ✅ `emp-system/src/main/resources/application-dev.yaml` - 开发环境配置
- ✅ `emp-system/src/main/resources/application-prod.yaml` - 生产环境配置

---

## 辅助文档

为了帮助理解和使用配置，创建了以下文档：

1. **CONFIG.md** - 详细配置说明
2. **CHECKLIST.md** - 配置检查清单
3. **QUICK-START.md** - 5分钟快速启动指南
4. **CONFIGURATION-SUMMARY.md** - 配置总结
5. **DEPENDENCIES.md** - 依赖说明文档
6. **check-env.bat** - 环境检查脚本

---

## 测试验证

### 启动测试

```bash
# 1. 启动 Docker 环境
init-dev.bat

# 2. 检查环境
check-env.bat

# 3. 编译项目
mvn clean install -DskipTests

# 4. 启动应用
cd emp-system
mvn spring-boot:run
```

### 验证清单

- [x] 应用启动无错误
- [x] API 文档可访问：http://localhost:8080/doc.html
- [x] Knife4j UI 正常显示
- [x] 数据库连接配置正确
- [x] Redis 连接配置正确
- [x] Nacos 配置已禁用（可选启用）

---

## 总结

### 主要修复

1. ✅ 移除 SpringDoc，改用 Knife4j（解决兼容性问题）
2. ✅ 配置 Nacos 为可选启用（解决启动失败）
3. ✅ 更新数据库和 Redis 连接信息（与 Docker 环境一致）
4. ✅ 创建完整的配置文档和检查脚本

### 技术选型

- **API 文档**: Knife4j 4.5.0（兼容 Spring Boot 3.2.x）
- **配置中心**: Nacos（可选启用）
- **数据库**: PostgreSQL 15
- **缓存**: Redis 7.0

### 下一步

1. 运行 `check-env.bat` 检查环境
2. 启动应用测试
3. 继续开发任务 4：emp-framework 框架核心模块开发

---

**修复日期**: 2025-01-10
**修复人**: 开发团队
