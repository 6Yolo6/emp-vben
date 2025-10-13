# EMP Platform 配置总结

## 配置文件清单

### 1. 核心配置文件

| 文件                  | 位置                           | 用途               | 状态     |
| --------------------- | ------------------------------ | ------------------ | -------- |
| bootstrap.yaml        | emp-system/src/main/resources/ | Nacos 配置中心引导 | ✓ 已创建 |
| application.yaml      | emp-system/src/main/resources/ | 应用主配置         | ✓ 已创建 |
| application-dev.yaml  | emp-system/src/main/resources/ | 开发环境配置       | ✓ 已创建 |
| application-prod.yaml | emp-system/src/main/resources/ | 生产环境配置       | ✓ 已创建 |

### 2. Docker 配置文件

| 文件               | 位置               | 用途                  | 状态     |
| ------------------ | ------------------ | --------------------- | -------- |
| docker-compose.yml | rear-emp-platform/ | Docker 服务编排       | ✓ 已创建 |
| init.sql           | docker/postgres/   | PostgreSQL 初始化脚本 | ✓ 已创建 |
| redis.conf         | docker/redis/      | Redis 配置            | ✓ 已创建 |

### 3. 项目配置文件

| 文件    | 位置               | 用途             | 状态     |
| ------- | ------------------ | ---------------- | -------- |
| pom.xml | rear-emp-platform/ | Maven 父工程配置 | ✓ 已创建 |
| pom.xml | emp-common/        | 公共模块配置     | ✓ 已创建 |
| pom.xml | emp-system/        | 系统管理模块配置 | ✓ 已创建 |

### 4. 辅助文件

| 文件           | 位置               | 用途                   | 状态     |
| -------------- | ------------------ | ---------------------- | -------- |
| init-dev.bat   | rear-emp-platform/ | Windows 环境初始化脚本 | ✓ 已创建 |
| check-env.bat  | rear-emp-platform/ | Windows 环境检查脚本   | ✓ 已创建 |
| CONFIG.md      | rear-emp-platform/ | 配置详细说明           | ✓ 已创建 |
| CHECKLIST.md   | rear-emp-platform/ | 配置检查清单           | ✓ 已创建 |
| QUICK-START.md | rear-emp-platform/ | 快速启动指南           | ✓ 已创建 |

## 配置要点

### 1. Nacos 配置（重要）

**当前状态**：已禁用（首次启动推荐）

**配置位置**：

- `bootstrap.yaml`: `spring.cloud.nacos.config.enabled: false`
- `application.yaml`: `spring.cloud.nacos.discovery.enabled: false`

**启用步骤**：

1. 确保 Docker 环境中 Nacos 正常运行
2. 修改配置文件，将 `enabled` 改为 `true`
3. 在 Nacos 控制台创建命名空间和配置文件
4. 重启应用

### 2. 数据库配置

**PostgreSQL 连接信息**：

```yaml
url: jdbc:postgresql://localhost:5432/emp_dev
username: emp
password: emp123456
```

**连接池配置**：

```yaml
hikari:
  maximum-pool-size: 20
  minimum-idle: 5
  idle-timeout: 300000
  connection-timeout: 20000
```

### 3. Redis 配置

**连接信息**：

```yaml
host: localhost
port: 6379
password: emp123456
database: 0
```

**连接池配置**：

```yaml
lettuce:
  pool:
    max-active: 8
    max-wait: -1ms
    max-idle: 8
    min-idle: 0
```

### 4. Sa-Token 配置

**Token 配置**：

```yaml
sa-token:
  token-name: Authorization
  timeout: 2592000 # 30天
  active-timeout: 1800 # 30分钟无操作
  is-concurrent: true # 允许并发登录
  is-share: false # 不共享 token
  token-style: uuid # UUID 风格
```

### 5. MyBatis-Flex 配置

**基础配置**：

```yaml
mybatis-flex:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    logic-delete-column: deleted
    logic-delete-value: 1
    logic-normal-value: 0
```

## 环境切换

### 开发环境（默认）

```yaml
spring:
  profiles:
    active: dev
```

启动命令：

```bash
mvn spring-boot:run
```

### 生产环境

```yaml
spring:
  profiles:
    active: prod
```

启动命令：

```bash
java -jar emp-system.jar --spring.profiles.active=prod
```

或使用环境变量：

```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar emp-system.jar
```

## 配置优先级

1. **命令行参数**（最高优先级）

   ```bash
   java -jar app.jar --server.port=8081
   ```

2. **环境变量**

   ```bash
   export SERVER_PORT=8081
   ```

3. **application-{profile}.yaml**

   - application-prod.yaml
   - application-dev.yaml

4. **application.yaml**

5. **bootstrap.yaml**

6. **Nacos 配置中心**（如果启用）

## 敏感信息处理

### 开发环境

敏感信息直接写在配置文件中（不提交到 Git）

### 生产环境

使用环境变量或配置中心：

```yaml
spring:
  datasource:
    password: ${SPRING_DATASOURCE_PASSWORD}
  data:
    redis:
      password: ${SPRING_REDIS_PASSWORD}
```

设置环境变量：

```bash
export SPRING_DATASOURCE_PASSWORD=your_password
export SPRING_REDIS_PASSWORD=your_password
```

## 配置验证

### 1. 检查配置文件语法

```bash
# 使用 YAML 验证工具
yamllint application.yaml
```

### 2. 检查配置加载

启动应用时查看日志：

```
The following profiles are active: dev
```

### 3. 检查配置值

访问 Actuator 端点（如果启用）：

```
http://localhost:8080/actuator/env
```

## 常见配置问题

### 问题 1: Nacos 配置导入失败

**错误信息**：

```
No spring.config.import property has been defined
```

**解决方案**：

1. 检查 `bootstrap.yaml` 中 Nacos 配置
2. 确认 `spring.cloud.nacos.config.enabled: false`
3. 或添加 `spring.config.import=optional:nacos:`

### 问题 2: 数据库连接失败

**错误信息**：

```
Connection refused
```

**解决方案**：

1. 检查 Docker 容器是否运行
2. 检查数据库连接信息
3. 测试数据库连接

### 问题 3: Redis 连接失败

**错误信息**：

```
Unable to connect to Redis
```

**解决方案**：

1. 检查 Docker 容器是否运行
2. 检查 Redis 密码
3. 测试 Redis 连接

### 问题 4: 配置文件未生效

**可能原因**：

- Profile 未正确激活
- 配置文件位置错误
- 配置语法错误

**解决方案**：

1. 检查 `spring.profiles.active` 配置
2. 确认配置文件在 `src/main/resources/` 目录
3. 验证 YAML 语法

## 配置最佳实践

### 1. 分离敏感信息

- 开发环境：使用本地配置
- 生产环境：使用环境变量或配置中心

### 2. 使用配置文件分层

```
application.yaml          # 公共配置
application-dev.yaml      # 开发环境
application-test.yaml     # 测试环境
application-prod.yaml     # 生产环境
```

### 3. 配置文件版本控制

- 提交：公共配置、模板配置
- 不提交：包含敏感信息的配置

### 4. 配置文档化

- 为每个配置项添加注释
- 维护配置说明文档
- 记录配置变更历史

### 5. 配置验证

- 启动时验证必要配置
- 使用 `@ConfigurationProperties` 验证
- 编写配置测试用例

## 下一步

配置完成后，可以：

1. 运行环境检查：`check-env.bat`
2. 启动应用：`mvn spring-boot:run`
3. 访问 API 文档：http://localhost:8080/doc.html
4. 开始开发任务：查看 `.kiro/specs/phase1-foundation/tasks.md`

## 参考文档

- [Spring Boot 配置文档](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
- [Spring Cloud Alibaba 文档](https://spring-cloud-alibaba-group.github.io/github-pages/2023/zh-cn/index.html)
- [Nacos 配置中心](https://nacos.io/zh-cn/docs/quick-start.html)
- [Sa-Token 文档](https://sa-token.cc/)
- [MyBatis-Flex 文档](https://mybatis-flex.com/)

---

**最后更新**: 2025-01-10
**维护人**: 开发团队
