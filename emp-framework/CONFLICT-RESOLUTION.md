# Bean冲突解决方案

## 问题描述

项目启动时出现Bean命名冲突错误：

```
项目里同时存在 com.ldjt.emp.common.core.redis.RedisCache 和 
com.ldjt.emp.framework.redis.RedisCache 两个类，
并且它们都被 Spring 扫描成同名 bean（redisCache），
冲突导致解析失败。
```

## 问题分析

### 冲突原因

1. **两个同名类：**
   - `com.ldjt.emp.common.core.redis.RedisCache`
   - `com.ldjt.emp.framework.redis.RedisCache`

2. **都使用@Component注解：**
   - Spring容器扫描时，默认使用类名首字母小写作为Bean名称
   - 两个类都会生成名为`redisCache`的Bean
   - 导致Bean定义冲突

3. **模块定位不清晰：**
   - emp-common应该是纯工具类模块
   - 不应该包含Spring组件

## 解决方案

### 方案选择

经过分析，采用**方案3：删除emp-common中的RedisCache**

### 方案对比

| 方案 | 优点 | 缺点 | 是否采纳 |
|------|------|------|----------|
| 1. 重命名其中一个类 | 简单快速 | 造成混淆，不符合规范 | ❌ |
| 2. 使用@Qualifier区分 | 保留两个类 | 增加复杂度，使用时需要指定 | ❌ |
| 3. 删除emp-common中的类 | 模块职责清晰 | 需要调整依赖 | ✅ |
| 4. 统一放在emp-common | 集中管理 | 违反模块定位原则 | ❌ |

### 实施步骤

1. **删除冲突类：**
   ```bash
   删除 rear-emp-platform/emp-common/src/main/java/com/ldjt/emp/common/core/redis/RedisCache.java
   ```

2. **统一使用emp-framework中的RedisCache：**
   ```java
   // 在业务模块中注入
   @Autowired
   private RedisCache redisCache;
   ```

3. **更新依赖：**
   - 确保业务模块依赖emp-framework
   - emp-framework已经依赖emp-common

4. **验证编译：**
   ```bash
   mvn clean install -DskipTests
   ```

## 架构原则

### emp-common模块定位

**应该包含：**
- ✅ 纯工具类（静态方法）
- ✅ 常量类
- ✅ 枚举类
- ✅ 基础实体类（POJO）
- ✅ 业务异常类

**不应该包含：**
- ❌ Spring配置类（@Configuration）
- ❌ Spring组件（@Component, @Service等）
- ❌ 需要注入Bean的类
- ❌ 框架相关的配置

### emp-framework模块定位

**应该包含：**
- ✅ Spring配置类
- ✅ Spring组件
- ✅ 框架工具类（需要注入Bean）
- ✅ Redis工具类
- ✅ 全局异常处理器
- ✅ 过滤器、拦截器

### 判断标准

**如何判断一个类应该放在哪个模块？**

```
是否需要注入Spring Bean？
├─ 是 → emp-framework
└─ 否 → 是否是Spring组件？
    ├─ 是 → emp-framework
    └─ 否 → emp-common
```

## 类似问题预防

### 1. 命名规范

- 避免在不同模块中创建同名类
- 如果必须同名，使用不同的包路径

### 2. 模块职责

- 明确每个模块的职责
- 遵循单一职责原则
- 参考 [ARCHITECTURE.md](../ARCHITECTURE.md)

### 3. 代码审查

- 新增类时检查是否有同名类
- 检查类的定位是否符合模块职责
- 检查是否需要使用Spring注解

### 4. 依赖管理

```
业务模块 → emp-framework → emp-common
```

- emp-common不依赖任何其他模块
- emp-framework依赖emp-common
- 业务模块依赖emp-framework和emp-common

## 验证结果

### 编译测试

```bash
mvn clean install -DskipTests
# BUILD SUCCESS
```

### Bean扫描

启动应用后，只有一个`redisCache` Bean：
```
com.ldjt.emp.framework.redis.RedisCache
```

### 功能验证

```java
@Autowired
private RedisCache redisCache;

// 正常使用
redisCache.set("key", "value");
```

## 相关文档

- [ARCHITECTURE.md](../ARCHITECTURE.md) - 架构说明
- [README.md](README.md) - 模块使用文档
- [IMPLEMENTATION.md](IMPLEMENTATION.md) - 实现总结

## 总结

通过删除emp-common中的RedisCache类，统一使用emp-framework中的版本，解决了Bean命名冲突问题。同时明确了模块职责，使架构更加清晰合理。

**核心原则：**
- emp-common = 纯工具类，无Spring依赖
- emp-framework = 框架组件，有Spring依赖
- Redis等基础设施统一在framework中管理
