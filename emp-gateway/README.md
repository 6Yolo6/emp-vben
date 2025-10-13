# EMP Gateway 网关服务

## 功能说明

网关服务是整个系统的统一入口，负责：

1. **路由转发**：将请求路由到对应的微服务
2. **鉴权认证**：基于 Sa-Token 的统一鉴权
3. **跨域处理**：统一处理跨域请求
4. **限流保护**：基于 Redis 的分布式限流
5. **日志记录**：记录所有请求日志

## 路由配置

### 系统服务路由
- 路径：`/system/**`
- 目标服务：`emp-system`
- 示例：`http://localhost:8888/system/user/list`

### 认证服务路由
- 路径：`/auth/**`
- 目标服务：`emp-auth`
- 示例：`http://localhost:8888/auth/login`

### 业务服务路由
- 路径：`/business/**`
- 目标服务：`emp-business`
- 示例：`http://localhost:8888/business/expert/list`

## 白名单配置

以下路径不需要鉴权：
- `/auth/login` - 登录接口
- `/auth/logout` - 登出接口
- `/auth/captcha` - 验证码接口
- `/system/user/register` - 用户注册接口
- `/druid/**` - Druid 监控页面
- `/actuator/**` - Spring Boot Actuator
- `/doc.html` - Knife4j 文档页面
- `/webjars/**` - 静态资源
- `/v3/api-docs/**` - OpenAPI 文档

## 限流配置

默认限流策略：
- 令牌桶填充速率：10 次/秒
- 令牌桶容量：20
- 限流维度：基于 IP 地址

可以通过配置文件调整限流策略，或使用不同的 KeyResolver：
- `ipKeyResolver`：基于 IP 限流
- `userKeyResolver`：基于用户 ID 限流
- `apiKeyResolver`：基于接口路径限流

## 启动说明

1. 确保 Redis 已启动
2. 确保 Nacos 已启动（如果启用服务发现）
3. 启动网关服务：
   ```bash
   mvn spring-boot:run
   ```
4. 访问地址：`http://localhost:8888`

## 测试示例

### 测试登录（无需鉴权）
```bash
curl -X POST http://localhost:8888/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 测试需要鉴权的接口
```bash
curl -X GET http://localhost:8888/system/user/list \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 测试限流
连续快速请求同一接口，超过限流阈值后会返回 429 状态码。

## 配置说明

主要配置项在 `application.yaml` 中：

- `server.port`：网关端口，默认 8888
- `spring.cloud.gateway.routes`：路由配置
- `spring.cloud.gateway.globalcors`：跨域配置
- `spring.cloud.gateway.default-filters`：全局过滤器配置
- `sa-token.*`：Sa-Token 鉴权配置
