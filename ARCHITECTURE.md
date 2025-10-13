# EMP平台架构说明

## 模块划分原则

### emp-common（公共模块）

**定位：** 纯工具类和基础定义，不包含Spring组件

**应该包含：**
- ✅ 工具类（DateUtils, StringUtils, BeanUtils等）
- ✅ 常量类（Constants）
- ✅ 枚举类（StatusEnum, DeletedEnum等）
- ✅ 基础实体类（BaseEntity, Result等）
- ✅ 业务异常类（BusinessException）
- ✅ 通用DTO/VO类

**不应该包含：**
- ❌ Spring配置类（@Configuration）
- ❌ Spring组件（@Component, @Service等）
- ❌ 需要注入其他Bean的类
- ❌ 框架相关的配置

**依赖关系：**
- 只依赖基础工具库（Hutool, Apache Commons等）
- 不依赖Spring Boot
- 不依赖数据库、Redis等基础设施

### emp-framework（框架核心模块）

**定位：** 框架级别的基础设施和Spring组件

**应该包含：**
- ✅ Spring配置类（RedisConfiguration, MyBatisFlexConfiguration等）
- ✅ 全局异常处理器（GlobalExceptionHandler）
- ✅ 过滤器（TraceIdFilter）
- ✅ 拦截器（Interceptor）
- ✅ 框架工具类（需要注入Bean的工具类）
- ✅ Redis工具类（RedisCache, DistributedLock）
- ✅ 安全工具类（SecurityUtils）
- ✅ 审计字段处理器（AuditFieldHandler）

**依赖关系：**
- 依赖emp-common
- 依赖Spring Boot
- 依赖Redis, MyBatis-Flex, Sa-Token等框架

### emp-system（系统管理服务）

**定位：** 业务服务模块

**应该包含：**
- ✅ Controller层
- ✅ Service层
- ✅ Mapper层
- ✅ Entity实体类
- ✅ DTO/VO类
- ✅ 业务逻辑实现

**依赖关系：**
- 依赖emp-common
- 依赖emp-framework
- 依赖数据库

## 架构决策记录

### ADR-001: Redis工具类放在emp-framework模块

**日期：** 2025-10-11

**状态：** 已采纳

**背景：**
项目中同时存在两个RedisCache类：
- `com.ldjt.emp.common.core.redis.RedisCache`
- `com.ldjt.emp.framework.redis.RedisCache`

这导致Spring容器中出现同名Bean冲突。

**决策：**
将Redis相关的所有类统一放在emp-framework模块中，删除emp-common中的Redis类。

**理由：**
1. RedisCache需要注入RedisTemplate（Spring Bean）
2. RedisConfiguration是Spring配置类，必须在framework中
3. RedissonConfiguration也是Spring配置类
4. emp-common应该保持纯工具类的定位，不包含Spring组件
5. Redis是框架级别的基础设施，应该由framework模块提供

**影响：**
- emp-common模块更加纯粹，只包含工具类
- emp-framework模块统一管理所有框架相关的配置和组件
- 避免Bean命名冲突
- 依赖关系更加清晰

**替代方案：**
1. 重命名其中一个类 - 不推荐，会造成混淆
2. 使用@Qualifier区分 - 不推荐，增加复杂度
3. 统一放在emp-common - 不推荐，违反模块定位原则

## 模块依赖关系图

```
emp-system ──┐
emp-auth   ──┼──> emp-framework ──> emp-common
emp-gateway ─┘
```

## 最佳实践

### 1. 新增工具类时的判断标准

**放在emp-common的条件：**
- 不需要注入任何Spring Bean
- 是纯静态方法或无状态的工具方法
- 不依赖Spring框架

**放在emp-framework的条件：**
- 需要注入Spring Bean
- 是Spring组件（@Component, @Service等）
- 是框架配置类（@Configuration）
- 依赖Spring框架特性

### 2. 避免循环依赖

- emp-common不应该依赖任何业务模块
- emp-framework不应该依赖业务模块
- 业务模块可以依赖emp-common和emp-framework

### 3. 包命名规范

```
com.ldjt.emp.common.utils      - 工具类
com.ldjt.emp.common.constants  - 常量类
com.ldjt.emp.common.enums      - 枚举类
com.ldjt.emp.common.core       - 核心基础类

com.ldjt.emp.framework.config  - 配置类
com.ldjt.emp.framework.filter  - 过滤器
com.ldjt.emp.framework.redis   - Redis相关
com.ldjt.emp.framework.mybatis - MyBatis相关
com.ldjt.emp.framework.security - 安全相关
```

## 常见问题

### Q1: 为什么不把所有工具类都放在emp-common？

A: emp-common应该保持纯粹，只包含不依赖Spring的工具类。需要注入Bean的工具类应该放在emp-framework。

### Q2: BaseEntity应该放在哪里？

A: BaseEntity是纯POJO类，不包含Spring注解，应该放在emp-common。但是审计字段的自动填充逻辑（AuditFieldHandler）应该放在emp-framework。

### Q3: 如果一个工具类既有静态方法又需要注入Bean怎么办？

A: 拆分成两个类：
- 静态方法部分放在emp-common
- 需要注入Bean的部分放在emp-framework

### Q4: 业务模块之间可以相互依赖吗？

A: 不推荐。如果需要共享业务逻辑，应该：
1. 提取到公共的业务模块
2. 通过RPC调用
3. 通过消息队列解耦

## 参考资料

- [Spring Boot最佳实践](https://spring.io/guides)
- [微服务架构设计模式](https://microservices.io/patterns/)
- [领域驱动设计](https://www.domainlanguage.com/ddd/)
