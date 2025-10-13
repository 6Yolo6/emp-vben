# EMP Platform 依赖说明

## 核心依赖版本

### 框架版本
- **Spring Boot**: 3.2.9
- **Spring Cloud**: 2023.0.3
- **Spring Cloud Alibaba**: 2023.0.3.3
- **Java**: 17

### 数据库相关
- **PostgreSQL Driver**: 42.7.7
- **HikariCP**: 5.0.1
- **MyBatis-Flex**: 1.11.3

### 认证授权
- **Sa-Token**: 1.44.0

### 缓存
- **Redis**: 7.0
- **Commons Pool2**: 2.11.1

### 工具库
- **Hutool**: 5.8.22
- **FastJson2**: 2.0.43
- **Lombok**: 1.18.30

### API 文档
- **Knife4j**: 4.5.0 ✓ (兼容 Spring Boot 3.2.x)
- ~~SpringDoc~~: ❌ (已移除，与 Spring Boot 3.2.x 不兼容)

### 工作流引擎
- **Flowable**: 7.0.1

### 其他工具
- **Redisson**: 3.27.2
- **Druid**: 1.2.23

## 依赖兼容性说明

### ✅ Knife4j vs SpringDoc

**问题**：SpringDoc 2.x 版本依赖 Spring Web 6.2+（Spring Boot 3.3.x），与 Spring Boot 3.2.9 不兼容。

**解决方案**：使用 Knife4j 4.5.0，它完全兼容 Spring Boot 3.2.x。

**Knife4j 优势**：
1. 完全兼容 Spring Boot 3.2.x
2. 基于 OpenAPI 3.0 规范
3. 提供更美观的 UI 界面
4. 支持中文界面
5. 功能更丰富（在线调试、文档导出等）

### 依赖关系图

```
emp-platform (父工程)
├── emp-common (公共模块)
│   ├── Spring Boot Starter
│   ├── Lombok
│   ├── Hutool
│   ├── FastJson2
│   ├── Knife4j ✓
│   └── MyBatis-Flex (注解)
│
├── emp-framework (框架核心)
│   ├── emp-common
│   ├── Spring Boot Web
│   ├── MyBatis-Flex
│   ├── Sa-Token
│   └── Redis
│
└── emp-system (系统管理)
    ├── emp-framework
    ├── PostgreSQL Driver
    ├── Knife4j ✓
    ├── Nacos Discovery
    ├── Nacos Config
    └── Spring Cloud Bootstrap
```

## API 文档使用

### Knife4j 访问地址

启动应用后访问：
- **文档地址**: http://localhost:8080/doc.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Knife4j 配置

```yaml
# application.yaml
knife4j:
  enable: true
  setting:
    language: zh_cn
```

### Knife4j 注解使用

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(summary = "查询用户", description = "根据ID查询用户信息")
    public Result<User> getUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        // ...
    }
}
```

### 常用注解对照表

| 功能 | Knife4j/OpenAPI 3.0 注解 | 说明 |
|------|-------------------------|------|
| 接口分组 | `@Tag` | 标注在 Controller 类上 |
| 接口说明 | `@Operation` | 标注在方法上 |
| 参数说明 | `@Parameter` | 标注在参数上 |
| 请求体说明 | `@RequestBody` + `@Schema` | 标注在 DTO 类和字段上 |
| 响应说明 | `@ApiResponse` | 标注在方法上 |
| 模型说明 | `@Schema` | 标注在实体类和字段上 |

## 依赖排除说明

### 已移除的依赖

1. **SpringDoc OpenAPI**
   - 原因：与 Spring Boot 3.2.9 不兼容
   - 替代方案：Knife4j 4.5.0
   - 影响：无，Knife4j 提供更好的功能

## 依赖更新建议

### 可以升级的依赖

1. **Spring Boot 3.3.x**（如果需要）
   - 需要同步升级所有 Spring 相关依赖
   - 可以使用 SpringDoc 替代 Knife4j
   - 建议：暂时保持 3.2.9，稳定性更好

2. **MyBatis-Flex**
   - 可以升级到最新版本
   - 向后兼容性好

3. **Hutool**
   - 可以升级到最新版本
   - 向后兼容性好

### 不建议升级的依赖

1. **Spring Boot**
   - 当前版本：3.2.9
   - 建议：保持当前版本，等待 3.3.x 稳定后再升级

2. **Spring Cloud Alibaba**
   - 当前版本：2023.0.3.3
   - 建议：与 Spring Boot 版本保持匹配

## 依赖冲突解决

### 常见冲突

1. **Slf4j 版本冲突**
   - 解决：使用 Spring Boot 管理的版本
   - 不要手动指定 Slf4j 版本

2. **Jackson 版本冲突**
   - 解决：使用 Spring Boot 管理的版本
   - FastJson2 与 Jackson 可以共存

3. **Servlet API 版本冲突**
   - 解决：使用 Jakarta Servlet API 6.0
   - 不要使用旧的 javax.servlet

## 依赖检查命令

### 查看依赖树

```bash
# 查看完整依赖树
mvn dependency:tree

# 查看特定模块的依赖树
cd emp-system
mvn dependency:tree

# 查看依赖冲突
mvn dependency:tree -Dverbose
```

### 分析依赖

```bash
# 分析依赖
mvn dependency:analyze

# 查看过期的依赖
mvn versions:display-dependency-updates

# 查看插件更新
mvn versions:display-plugin-updates
```

## 依赖下载加速

### 配置 Maven 镜像

编辑 `~/.m2/settings.xml`：

```xml
<mirrors>
    <mirror>
        <id>aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>Aliyun Maven</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

### 清理本地仓库

```bash
# 清理本地仓库
mvn dependency:purge-local-repository

# 强制更新
mvn clean install -U
```

## 依赖安全检查

### 检查漏洞

```bash
# 使用 OWASP Dependency Check
mvn org.owasp:dependency-check-maven:check

# 查看安全报告
open target/dependency-check-report.html
```

## 参考文档

- [Spring Boot 3.2.9 文档](https://docs.spring.io/spring-boot/docs/3.2.9/reference/html/)
- [Spring Cloud 2023.0.3 文档](https://spring.io/projects/spring-cloud)
- [Spring Cloud Alibaba 文档](https://spring-cloud-alibaba-group.github.io/github-pages/2023/zh-cn/index.html)
- [Knife4j 文档](https://doc.xiaominfo.com/)
- [MyBatis-Flex 文档](https://mybatis-flex.com/)
- [Sa-Token 文档](https://sa-token.cc/)

---

**最后更新**: 2025-01-10
**维护人**: 开发团队
