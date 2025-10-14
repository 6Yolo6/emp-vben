# EMP Platform 集成测试指南

## 测试环境准备

### 1. 启动基础服务

```bash
# 启动 Docker 服务（PostgreSQL、Redis）
cd rear-emp-platform
init-dev.bat
```

等待所有服务启动完成，确认：
- PostgreSQL: localhost:5432
- Redis: localhost:6379

### 2. 启动应用服务

```bash
# 方式1：使用启动脚本
start-all.bat

# 方式2：手动启动各服务
cd emp-system
mvn spring-boot:run

cd emp-auth
mvn spring-boot:run

cd emp-gateway
mvn spring-boot:run
```

## 测试场景

### 场景1：验证码获取测试

**请求：**
```bash
curl -X GET http://localhost:8888/auth/captcha
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "uuid": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "img": "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
  }
}
```

### 场景2：用户登录测试

**请求：**
```bash
curl -X POST http://localhost:8888/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "code": "1234",
    "uuid": "验证码UUID"
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "tokenType": "Bearer",
    "expiresIn": 2592000
  }
}
```

### 场景3：Token验证测试

**请求：**
```bash
curl -X GET http://localhost:8888/system/user/info \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    ...
  }
}
```

### 场景4：用户列表查询测试

**请求：**
```bash
curl -X GET "http://localhost:8888/system/user/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [...],
    "total": 10,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 场景5：用户登出测试

**请求：**
```bash
curl -X POST http://localhost:8888/auth/logout \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功"
}
```

### 场景6：Token刷新测试

**请求：**
```bash
curl -X POST http://localhost:8888/auth/refresh \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "tokenType": "Bearer",
    "expiresIn": 2592000
  }
}
```

## API文档访问测试

### 1. emp-system API文档
访问：http://localhost:8080/doc.html

验证点：
- 文档页面正常加载
- 可以看到所有API接口
- 可以在线测试接口

### 2. emp-auth API文档
访问：http://localhost:8081/doc.html

验证点：
- 文档页面正常加载
- 可以看到认证相关接口
- 验证码、登录、登出接口可见

### 3. emp-gateway API文档
访问：http://localhost:8888/doc.html

验证点：
- 网关聚合文档正常显示
- 可以切换不同服务的文档

## 权限控制测试

### 1. 无Token访问测试

**请求：**
```bash
curl -X GET http://localhost:8888/system/user/list
```

**预期响应：**
```json
{
  "code": 401,
  "message": "未登录或Token已过期"
}
```

### 2. 错误Token访问测试

**请求：**
```bash
curl -X GET http://localhost:8888/system/user/list \
  -H "Authorization: Bearer invalid_token"
```

**预期响应：**
```json
{
  "code": 401,
  "message": "Token无效"
}
```

## 网关路由测试

### 1. 系统服务路由
- 路径：`/system/**`
- 目标：emp-system (localhost:8080)

### 2. 认证服务路由
- 路径：`/auth/**`
- 目标：emp-auth (localhost:8081)

## 测试检查清单

- [ ] Docker服务正常启动（PostgreSQL、Redis）
- [ ] emp-system服务正常启动
- [ ] emp-auth服务正常启动
- [ ] emp-gateway服务正常启动
- [ ] 验证码生成功能正常
- [ ] 用户登录功能正常
- [ ] Token验证功能正常
- [ ] 用户登出功能正常
- [ ] Token刷新功能正常
- [ ] API文档可正常访问
- [ ] 网关路由转发正常
- [ ] 权限控制正常工作
- [ ] 跨域配置正常工作

## 常见问题排查

### 1. 服务启动失败
- 检查端口是否被占用
- 检查数据库连接是否正常
- 检查Redis连接是否正常

### 2. 登录失败
- 检查验证码是否正确
- 检查用户名密码是否正确
- 检查用户状态是否正常

### 3. Token验证失败
- 检查Token是否过期
- 检查Token格式是否正确
- 检查Redis中是否存在Token

### 4. 网关路由失败
- 检查网关配置是否正确
- 检查目标服务是否启动
- 检查服务注册是否成功
