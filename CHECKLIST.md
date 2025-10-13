# EMP Platform 配置检查清单

## 启动前检查

### 1. 环境准备 ✓

- [ ] JDK 17+ 已安装
- [ ] Maven 3.8+ 已安装
- [ ] Docker Desktop 已安装并运行
- [ ] Docker Compose 已安装

### 2. Docker 服务启动 ✓

- [ ] 运行 `init-dev.bat` (Windows) 或 `init-dev.sh` (Linux/Mac)
- [ ] PostgreSQL 容器运行正常 (端口 5432)
- [ ] Redis 容器运行正常 (端口 6379)
- [ ] Nacos 容器运行正常 (端口 8848)

验证命令：
```bash
docker ps
```

### 3. 服务连接测试 ✓

#### PostgreSQL
```bash
docker exec -it emp-postgres psql -U emp -d emp_dev -c "SELECT version();"
```

#### Redis
```bash
docker exec -it emp-redis redis-cli -a emp123456 ping
```

#### Nacos
访问：http://localhost:8848/nacos
- 用户名：nacos
- 密码：nacos

### 4. 配置文件检查 ✓

#### bootstrap.yaml
- [ ] `spring.application.name` 已设置
- [ ] `spring.cloud.nacos.server-addr` 地址正确
- [ ] `spring.cloud.nacos.config.enabled` 设置为 `false`（首次启动）

#### application.yaml
- [ ] 数据库连接信息正确
  - url: `jdbc:postgresql://localhost:5432/emp_dev`
  - username: `emp`
  - password: `emp123456`
- [ ] Redis 连接信息正确
  - host: `localhost`
  - port: `6379`
  - password: `emp123456`
- [ ] Nacos 服务发现已禁用（首次启动）
  - `spring.cloud.nacos.discovery.enabled: false`
- [ ] Nacos 配置中心已禁用（首次启动）
  - `spring.cloud.nacos.config.enabled: false`

### 5. 依赖检查 ✓

```bash
# 检查依赖
mvn dependency:tree

# 下载依赖
mvn clean install -DskipTests
```

- [ ] 所有依赖下载成功
- [ ] 编译无错误

### 6. 数据库初始化 ✓

- [ ] 数据库 `emp_dev` 已创建
- [ ] 初始化脚本已执行
- [ ] 表结构创建成功（如果有）

验证：
```bash
docker exec -it emp-postgres psql -U emp -d emp_dev -c "\dt"
```

## 启动检查

### 1. 应用启动 ✓

```bash
cd emp-system
mvn spring-boot:run
```

或使用 IDE 启动 `EmpSystemApplication`

- [ ] 应用启动无错误
- [ ] 端口 8080 监听成功
- [ ] 日志无异常信息

### 2. 健康检查 ✓

访问：http://localhost:8080/actuator/health

预期响应：
```json
{
  "status": "UP"
}
```

### 3. API 文档访问 ✓

访问：http://localhost:8080/doc.html

- [ ] Knife4j 文档页面正常显示
- [ ] API 接口列表正常加载

### 4. 基础接口测试 ✓

#### 测试登录接口（如果已实现）
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 常见问题排查

### 问题 1: 启动报错 "No spring.config.import property has been defined"

**原因**: Nacos 配置中心未禁用

**解决方案**:
1. 检查 `bootstrap.yaml` 中 `spring.cloud.nacos.config.enabled` 是否为 `false`
2. 检查 `application.yaml` 中 `spring.cloud.nacos.config.enabled` 是否为 `false`

### 问题 2: 数据库连接失败

**原因**: PostgreSQL 未启动或连接信息错误

**解决方案**:
1. 检查 Docker 容器状态：`docker ps`
2. 检查数据库连接信息是否正确
3. 测试数据库连接：`docker exec -it emp-postgres psql -U emp -d emp_dev`

### 问题 3: Redis 连接失败

**原因**: Redis 未启动或密码错误

**解决方案**:
1. 检查 Docker 容器状态：`docker ps`
2. 测试 Redis 连接：`docker exec -it emp-redis redis-cli -a emp123456 ping`
3. 检查 `application.yaml` 中 Redis 密码是否正确

### 问题 4: 端口被占用

**原因**: 8080 端口已被其他应用占用

**解决方案**:
1. 查看端口占用：`netstat -ano | findstr "8080"` (Windows)
2. 修改 `application.yaml` 中的 `server.port`
3. 或停止占用端口的应用

### 问题 5: Maven 依赖下载失败

**原因**: 网络问题或仓库配置问题

**解决方案**:
1. 配置 Maven 镜像（阿里云）
2. 清理本地仓库：`mvn dependency:purge-local-repository`
3. 强制更新：`mvn clean install -U`

## 启用 Nacos（可选）

当 Docker 环境稳定运行后，可以启用 Nacos：

### 1. 修改配置文件

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

### 2. 在 Nacos 中创建配置

1. 访问 Nacos 控制台：http://localhost:8848/nacos
2. 创建命名空间：dev
3. 创建配置文件：application-common.yaml

### 3. 重启应用

```bash
mvn spring-boot:run
```

## 验收标准

- [ ] Docker 环境正常运行
- [ ] 应用启动无错误
- [ ] API 文档可正常访问
- [ ] 数据库连接正常
- [ ] Redis 连接正常
- [ ] 日志输出正常
- [ ] 基础接口测试通过

## 下一步

配置检查完成后，可以开始：

1. 查看任务清单：`.kiro/specs/phase1-foundation/tasks.md`
2. 开始开发第一个任务
3. 参考设计文档：`.kiro/specs/phase1-foundation/design.md`

---

**提示**: 建议按照此清单逐项检查，确保环境配置正确后再开始开发。
