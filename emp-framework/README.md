# emp-framework 框架核心模块

## 模块说明

emp-framework是EMP企业管理平台的框架核心模块，提供系统的基础功能和横切关注点处理。

## 主要功能

### 1. 全局异常处理

- **GlobalExceptionHandler**: 统一异常处理器
  - 业务异常处理（BusinessException）
  - 参数校验异常处理（MethodArgumentNotValidException、BindException）
  - 约束违反异常处理（ConstraintViolationException）
  - 系统异常处理（Exception）

### 2. MDC链路追踪

- **TraceIdFilter**: TraceId过滤器
  - 自动生成或从请求头获取TraceId
  - 将TraceId放入MDC，实现日志链路追踪
  - 将TraceId添加到响应头，方便前端追踪

### 3. Logback日志配置

- **logback-spring.xml**: 日志配置文件
  - 支持按日期滚动（每天一个文件）
  - 支持按大小分割（单文件最大100MB）
  - 分级别输出（INFO、WARN、ERROR）
  - 异步日志输出，提高性能
  - 支持多环境配置（dev、test、prod）
  - 日志格式包含TraceId，便于链路追踪

### 4. MyBatis-Flex配置

- **MyBatisFlexConfiguration**: MyBatis-Flex全局配置
  - 配置审计字段自动填充
  - 配置分页插件
  - 配置逻辑删除插件
  - 开发环境开启SQL审计

- **AuditFieldHandler**: 审计字段自动填充处理器
  - 插入时自动填充createBy、createTime、updateBy、updateTime
  - 更新时自动填充updateBy、updateTime
  - 自动获取当前登录用户ID

### 5. Redis配置

- **RedisConfiguration**: Redis配置类
  - 配置RedisTemplate
  - 使用Jackson序列化
  - 支持Java 8时间类型

- **RedisCache**: Redis缓存工具类
  - 提供常用的缓存操作方法
  - 支持String、Hash、Set、List等数据结构
  - 支持过期时间设置

- **RedissonConfiguration**: Redisson配置类
  - 配置Redisson客户端
  - 支持分布式锁

- **DistributedLock**: 分布式锁工具类
  - 提供加锁、解锁、尝试加锁等方法
  - 基于Redisson实现

### 6. 安全工具类

- **SecurityUtils**: 安全工具类
  - 获取当前登录用户ID
  - 判断是否已登录
  - 基于Sa-Token实现

## 配置说明

### application.yaml配置项

```yaml
spring:
  data:
    redis:
      host: localhost          # Redis主机
      port: 6379              # Redis端口
      password: emp123456     # Redis密码
      database: 0             # Redis数据库

mybatis-flex:
  global-config:
    logic-delete-column: deleted      # 逻辑删除字段
    logic-delete-value: 1             # 逻辑删除值
    logic-not-delete-value: 0         # 逻辑未删除值
    key-type: snowflake_id            # 主键类型

sa-token:
  token-name: Authorization           # Token名称
  timeout: 2592000                    # Token有效期（30天）
  activity-timeout: 1800              # 临时有效期（30分钟）
  is-concurrent: true                 # 允许并发登录
  token-style: uuid                   # Token风格
```

## 使用示例

### 1. 使用Redis缓存

```java
@Autowired
private RedisCache redisCache;

// 设置缓存
redisCache.set("key", "value", 10, TimeUnit.MINUTES);

// 获取缓存
String value = redisCache.get("key");

// 删除缓存
redisCache.delete("key");
```

### 2. 使用分布式锁

```java
@Autowired
private DistributedLock distributedLock;

// 加锁
RLock lock = distributedLock.lock("lockKey");
try {
    // 业务逻辑
} finally {
    distributedLock.unlock(lock);
}

// 尝试加锁
if (distributedLock.tryLock("lockKey", 10, 30, TimeUnit.SECONDS)) {
    try {
        // 业务逻辑
    } finally {
        distributedLock.unlock("lockKey");
    }
}
```

### 3. 获取当前登录用户

```java
// 获取当前登录用户ID
Long userId = SecurityUtils.getUserId();

// 获取当前登录用户ID（必须登录）
Long userId = SecurityUtils.getRequiredUserId();

// 判断是否已登录
boolean isLogin = SecurityUtils.isLogin();
```

### 4. 审计字段自动填充

实体类需要实现BaseEntity接口或继承包含审计字段的基类：

```java
@Data
public class SysUser implements BaseEntity {
    private Long id;
    private String username;
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;
    private Integer deleted;
    
    // 实现BaseEntity接口方法
    // ...
}
```

## 依赖说明

- Spring Boot 3.2.9
- MyBatis-Flex 1.11.3
- Sa-Token 1.44.0
- Redisson 3.35.0
- Druid 1.2.23

## 架构说明

### 模块定位

emp-framework是框架核心模块，包含所有需要Spring容器管理的基础设施组件：
- Spring配置类（@Configuration）
- Spring组件（@Component）
- 需要注入Bean的工具类

### 与emp-common的区别

**emp-common：** 纯工具类和基础定义，不包含Spring组件
- 工具类（DateUtils, StringUtils等）
- 常量、枚举
- 基础实体类（BaseEntity, Result等）

**emp-framework：** 框架级别的基础设施和Spring组件
- Redis工具类（RedisCache, DistributedLock）
- 全局异常处理器
- 过滤器、拦截器
- 框架配置类

详见项目根目录的 [ARCHITECTURE.md](../ARCHITECTURE.md)

## 注意事项

1. 使用审计字段自动填充功能时，实体类必须实现BaseEntity接口
2. TraceId会自动添加到日志中，格式为：`[traceId]`
3. Redis密码可以为空，但生产环境建议设置密码
4. 分布式锁使用完毕后必须释放，建议使用try-finally确保释放
5. 日志文件默认保存在`./logs`目录下，保留30天
6. **Redis相关的类统一在emp-framework模块中，不要在emp-common中创建**
