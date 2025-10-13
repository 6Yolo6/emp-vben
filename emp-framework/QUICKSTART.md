# emp-framework 快速启动指南

## 前置条件

1. **JDK 17+** 已安装
2. **Maven 3.6+** 已安装
3. **Redis 7.0+** 已启动
4. **PostgreSQL 15+** 已启动

## 快速开始

### 1. 编译安装

```bash
cd rear-emp-platform/emp-framework
mvn clean install -DskipTests
```

### 2. 在其他模块中使用

在需要使用框架功能的模块（如emp-system）的pom.xml中添加依赖：

```xml
<dependency>
    <groupId>com.ldjt.emp</groupId>
    <artifactId>emp-framework</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 3. 配置application.yaml

```yaml
spring:
  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0

# MyBatis-Flex配置
mybatis-flex:
  global-config:
    logic-delete-column: deleted
    logic-delete-value: 1
    logic-not-delete-value: 0
    key-type: snowflake_id

# Sa-Token配置
sa-token:
  token-name: Authorization
  timeout: 2592000
  activity-timeout: 1800
```

### 4. 启动应用

```bash
cd rear-emp-platform/emp-system
mvn spring-boot:run
```

## 核心功能使用

### 全局异常处理

自动生效，无需额外配置。所有异常都会被GlobalExceptionHandler捕获并返回统一格式。

### MDC链路追踪

自动生效，每个请求都会自动生成TraceId并添加到日志中。

查看日志：
```bash
tail -f logs/emp-platform-info.log
```

### Redis缓存

```java
@Autowired
private RedisCache redisCache;

// 设置缓存
redisCache.set("key", "value", 10, TimeUnit.MINUTES);

// 获取缓存
String value = redisCache.get("key");
```

### 分布式锁

```java
@Autowired
private DistributedLock distributedLock;

RLock lock = distributedLock.lock("lockKey");
try {
    // 业务逻辑
} finally {
    distributedLock.unlock(lock);
}
```

### 审计字段自动填充

```java
// 实体类实现BaseEntity接口
@Data
public class SysUser implements BaseEntity {
    private Long id;
    private String username;
    private Long createBy;      // 自动填充
    private LocalDateTime createTime;  // 自动填充
    private Long updateBy;      // 自动填充
    private LocalDateTime updateTime;  // 自动填充
    
    // 实现接口方法...
}
```

### 获取当前登录用户

```java
// 获取当前登录用户ID
Long userId = SecurityUtils.getUserId();

// 判断是否已登录
boolean isLogin = SecurityUtils.isLogin();
```

## 常见问题

### Q1: 启动时报Logback配置错误

**A:** 确保logback-spring.xml中包含了Spring Boot默认配置引入：
```xml
<include resource="org/springframework/boot/logging/logback/defaults.xml"/>
```

### Q2: Redis连接失败

**A:** 
1. 检查Redis是否已启动：`docker ps | grep redis`
2. 检查Redis密码是否正确
3. 使用docker-compose启动：`docker-compose up -d redis`

### Q3: 审计字段没有自动填充

**A:** 
1. 确保实体类实现了BaseEntity接口
2. 确保用户已登录（SecurityUtils.getUserId()能获取到用户ID）
3. 检查MyBatis-Flex配置是否正确

### Q4: 日志文件没有生成

**A:** 
1. 检查logs目录是否有写入权限
2. 检查logback-spring.xml配置是否正确
3. 确保应用已正常启动

## 验证清单

- [ ] 编译成功
- [ ] 应用启动成功
- [ ] 日志文件正常生成
- [ ] TraceId出现在日志中
- [ ] Redis连接成功
- [ ] 全局异常处理生效
- [ ] 审计字段自动填充

## 下一步

emp-framework模块已就绪，可以继续开发：
- emp-system（系统管理服务）
- emp-auth（认证服务）
- emp-gateway（网关服务）

## 相关文档

- [README.md](README.md) - 模块说明和使用文档
- [IMPLEMENTATION.md](IMPLEMENTATION.md) - 实现总结
- [TESTING.md](TESTING.md) - 测试指南
