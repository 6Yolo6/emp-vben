# emp-framework 测试指南

## 编译测试

### 编译命令

```bash
cd rear-emp-platform/emp-framework
mvn clean install -DskipTests
```

### 预期结果

```
[INFO] BUILD SUCCESS
```

## 功能验证

由于emp-framework是一个框架模块，需要在实际的应用中验证功能。以下是验证步骤：

### 1. 验证全局异常处理

在emp-system或其他服务模块中：

```java
@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/business-exception")
    public Result<?> testBusinessException() {
        throw new BusinessException(400, "这是一个业务异常");
    }
    
    @GetMapping("/system-exception")
    public Result<?> testSystemException() {
        throw new RuntimeException("这是一个系统异常");
    }
    
    @PostMapping("/validation")
    public Result<?> testValidation(@Valid @RequestBody UserDTO dto) {
        return Result.success(dto);
    }
}
```

**预期结果：**
- 业务异常返回：`{"code": 400, "message": "这是一个业务异常", ...}`
- 系统异常返回：`{"code": 500, "message": "系统异常，请联系管理员", ...}`
- 参数校验异常返回具体的字段错误信息

### 2. 验证MDC链路追踪

启动应用后，查看日志文件：

```bash
tail -f logs/emp-platform-info.log
```

**预期结果：**
日志中包含TraceId：
```
2025-10-11 11:47:30.123 [http-nio-8080-exec-1] [a1b2c3d4e5f6g7h8] INFO  c.l.e.controller.TestController - 测试日志
```

### 3. 验证Redis缓存

```java
@Autowired
private RedisCache redisCache;

@GetMapping("/test-redis")
public Result<?> testRedis() {
    // 设置缓存
    redisCache.set("test:key", "test value", 10, TimeUnit.MINUTES);
    
    // 获取缓存
    String value = redisCache.get("test:key");
    
    return Result.success(value);
}
```

**预期结果：**
- 返回：`{"code": 200, "data": "test value", ...}`
- Redis中存在key：`test:key`

### 4. 验证分布式锁

```java
@Autowired
private DistributedLock distributedLock;

@GetMapping("/test-lock")
public Result<?> testLock() {
    RLock lock = distributedLock.lock("test:lock");
    try {
        // 业务逻辑
        Thread.sleep(5000);
        return Result.success("加锁成功");
    } catch (Exception e) {
        return Result.error(500, e.getMessage());
    } finally {
        distributedLock.unlock(lock);
    }
}
```

**预期结果：**
- 第一个请求正常执行
- 并发请求会等待锁释放

### 5. 验证审计字段自动填充

创建一个实体类并保存：

```java
@Data
public class TestEntity implements BaseEntity {
    private Long id;
    private String name;
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;
    
    // 实现BaseEntity接口方法...
}

// 保存实体
testMapper.insert(testEntity);
```

**预期结果：**
- createBy、createTime、updateBy、updateTime字段自动填充
- 数据库中可以看到这些字段的值

### 6. 验证日志文件

检查日志文件是否正常生成：

```bash
ls -lh logs/
```

**预期结果：**
```
emp-platform-info.log
emp-platform-warn.log
emp-platform-error.log
```

## 启动问题排查

### 问题1：Logback配置错误

**错误信息：**
```
There is no conversion class registered for composite conversion word [clr]
```

**解决方案：**
已在logback-spring.xml中添加Spring Boot默认配置引入：
```xml
<include resource="org/springframework/boot/logging/logback/defaults.xml"/>
```

### 问题2：Redis连接失败

**错误信息：**
```
Unable to connect to Redis
```

**解决方案：**
1. 确保Redis服务已启动
2. 检查application.yaml中的Redis配置
3. 使用docker-compose启动Redis：
```bash
docker-compose up -d redis
```

### 问题3：MyBatis-Flex配置错误

**错误信息：**
```
找不到符号 getGlobalConfig()
```

**解决方案：**
已修复，使用`FlexGlobalConfig.getDefaultConfig()`

## 集成测试

在emp-system模块中添加依赖：

```xml
<dependency>
    <groupId>com.ldjt.emp</groupId>
    <artifactId>emp-framework</artifactId>
    <version>${project.version}</version>
</dependency>
```

然后启动emp-system服务进行完整测试。

## 注意事项

1. **环境要求：**
   - JDK 17+
   - Redis 7.0+
   - PostgreSQL 15+

2. **配置要求：**
   - 必须配置Redis连接信息
   - 必须配置数据库连接信息
   - 建议配置spring.profiles.active

3. **依赖顺序：**
   - 先启动Redis和PostgreSQL
   - 再启动应用服务

4. **日志位置：**
   - 默认日志目录：`./logs`
   - 可通过LOG_PATH环境变量修改

## 性能测试

### Redis缓存性能

```bash
# 使用JMeter或ab工具测试
ab -n 10000 -c 100 http://localhost:8080/test/redis
```

### 分布式锁性能

```bash
# 测试并发加锁
ab -n 1000 -c 50 http://localhost:8080/test/lock
```

## 下一步

emp-framework模块已完成，可以继续执行：
- 任务5：核心数据库表设计和创建
- 任务6：Sa-Token认证授权集成
