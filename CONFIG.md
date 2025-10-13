# EMP Platform 配置说明

## 配置文件结构

### 1. bootstrap.yaml
- **作用**: Spring Cloud 应用的引导配置文件，优先于 application.yaml 加载
- **用途**: 主要用于配置 Nacos 配置中心的连接信息
- **位置**: `src/main/resources/bootstrap.yaml`

### 2. application.yaml
- **作用**: 应用主配置文件
- **用途**: 配置数据库、Redis、Sa-Token、MyBatis-Flex 等
- **位置**: `src/main/resources/application.yaml`

## 配置启用步骤

### 第一步：启动 Docker 环境

```bash
# Windows
init-dev.bat

# Linux/Mac
./init-dev.sh
```

确保以下服务正常运行：
- PostgreSQL (localhost:5432)
- Redis (localhost:6379)
- Nacos (http://localhost:8848/nacos)

### 第二步：启用 Nacos 配置

当 Docker 环境启动成功后，修改配置文件：

#### bootstrap.yaml
```yaml
spring:
  cloud:
    nacos:
      config:
        enabled: true  # 改为 true
```

#### application.yaml
```yaml
spring:
  cloud:
    nacos:
      discovery:
        enabled: true  # 改为 true
      config:
        enabled: true  # 改为 true
```

### 第三步：在 Nacos 中创建配置

访问 Nacos 控制台: http://localhost:8848/nacos
- 用户名: nacos
- 密码: nacos

创建以下配置：

#### 1. 命名空间
- 命名空间ID: dev
- 命名空间名: 开发环境

#### 2. 配置文件
在 dev 命名空间下创建：

**Data ID**: application-common.yaml
**Group**: DEFAULT_GROUP
**配置格式**: YAML
**配置内容**:
```yaml
# 公共配置
spring:
  jackson:
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss
    serialization:
      write-dates-as-timestamps: false
```

## 配置项说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/emp_dev
    username: emp
    password: emp123456
    driver-class-name: org.postgresql.Driver
```

### Redis 配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: emp123456
      database: 0
```

### Sa-Token 配置
```yaml
sa-token:
  token-name: Authorization
  timeout: 2592000  # 30天
  active-timeout: 1800  # 30分钟无操作
  is-concurrent: true
  is-share: false
  token-style: uuid
```

### MyBatis-Flex 配置
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

## 常见问题

### 1. 启动报错：No spring.config.import property has been defined

**原因**: Nacos 配置中心未启动或配置未正确设置

**解决方案**:
1. 确保 Docker 环境已启动
2. 检查 bootstrap.yaml 中 Nacos 地址是否正确
3. 暂时禁用 Nacos 配置中心（设置 enabled: false）

### 2. 数据库连接失败

**原因**: PostgreSQL 未启动或连接信息错误

**解决方案**:
1. 检查 Docker 容器是否运行: `docker ps`
2. 检查数据库连接信息是否正确
3. 测试数据库连接: `docker exec -it emp-postgres psql -U emp -d emp_dev`

### 3. Redis 连接失败

**原因**: Redis 未启动或密码错误

**解决方案**:
1. 检查 Docker 容器是否运行: `docker ps`
2. 测试 Redis 连接: `docker exec -it emp-redis redis-cli -a emp123456 ping`

## 环境变量

可以通过环境变量覆盖配置：

```bash
# 数据库配置
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/emp_dev
export SPRING_DATASOURCE_USERNAME=emp
export SPRING_DATASOURCE_PASSWORD=emp123456

# Redis 配置
export SPRING_REDIS_HOST=localhost
export SPRING_REDIS_PORT=6379
export SPRING_REDIS_PASSWORD=emp123456

# Nacos 配置
export SPRING_CLOUD_NACOS_SERVER_ADDR=localhost:8848
export SPRING_CLOUD_NACOS_USERNAME=nacos
export SPRING_CLOUD_NACOS_PASSWORD=nacos
```

## 配置优先级

1. 命令行参数
2. 环境变量
3. bootstrap.yaml
4. application.yaml
5. Nacos 配置中心

## 开发建议

1. **本地开发**: 禁用 Nacos，使用本地配置文件
2. **测试环境**: 启用 Nacos，使用 dev 命名空间
3. **生产环境**: 启用 Nacos，使用 prod 命名空间，配置敏感信息加密
