# EMP Platform 快速启动指南

## 5分钟快速启动

### 步骤 1: 启动 Docker 环境（2分钟）

```bash
# Windows
init-dev.bat

# Linux/Mac
chmod +x init-dev.sh
./init-dev.sh
```

等待所有服务启动完成。

### 步骤 2: 检查环境（1分钟）

```bash
# Windows
check-env.bat

# Linux/Mac
chmod +x check-env.sh
./check-env.sh
```

确保所有检查项都显示 ✓

### 步骤 3: 编译项目（1分钟）

```bash
mvn clean install -DskipTests
```

### 步骤 4: 启动应用（1分钟）

```bash
cd emp-system
mvn spring-boot:run
```

或使用 IDE 启动 `com.ldjt.emp.EmpSystemApplication`

### 步骤 5: 验证

访问 API 文档：http://localhost:8080/doc.html

## 服务地址

| 服务 | 地址 | 账号/密码 |
|------|------|-----------|
| API文档 | http://localhost:8080/doc.html | - |
| Nacos | http://localhost:8848/nacos | nacos/nacos |
| PostgreSQL | localhost:5432 | emp/emp123456 |
| Redis | localhost:6379 | emp123456 |

## 常用命令

### Docker 管理

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 应用管理

```bash
# 编译
mvn clean install

# 启动
mvn spring-boot:run

# 测试
mvn test

# 打包
mvn package
```

## 故障排除

### 问题：启动报错 "No spring.config.import"

**解决**：Nacos 配置已禁用，这是正常的。如果仍报错，检查：
- `bootstrap.yaml` 中 `spring.cloud.nacos.config.enabled: false`
- `application.yaml` 中 `spring.cloud.nacos.config.enabled: false`

### 问题：数据库连接失败

**解决**：
```bash
# 检查 PostgreSQL 是否运行
docker ps | grep postgres

# 测试连接
docker exec -it emp-postgres psql -U emp -d emp_dev
```

### 问题：Redis 连接失败

**解决**：
```bash
# 检查 Redis 是否运行
docker ps | grep redis

# 测试连接
docker exec -it emp-redis redis-cli -a emp123456 ping
```

### 问题：端口被占用

**解决**：
```bash
# Windows 查看端口占用
netstat -ano | findstr "8080"

# 修改端口（application.yaml）
server:
  port: 8081  # 改为其他端口
```

## 开发流程

1. **查看任务**：`.kiro/specs/phase1-foundation/tasks.md`
2. **参考设计**：`.kiro/specs/phase1-foundation/design.md`
3. **开发代码**：按照任务清单逐个完成
4. **运行测试**：`mvn test`
5. **提交代码**：遵循 Git 提交规范

## 项目结构

```
emp-platform/
├── emp-common/          # 公共模块（已完成基础功能）
│   ├── domain/         # 实体类（Result、BaseEntity）
│   ├── exception/      # 异常类（BusinessException）
│   ├── enums/          # 枚举类（StatusEnum、DeletedEnum）
│   └── utils/          # 工具类（DateUtils、StringUtils、BeanUtils）
├── emp-framework/       # 框架核心（待开发）
├── emp-system/         # 系统管理（当前开发）
└── docker/             # Docker 配置
```

## 下一步

- [ ] 完成 emp-framework 模块开发（任务 4）
- [ ] 创建核心数据库表（任务 5）
- [ ] 集成 Sa-Token 认证（任务 6）

详细任务列表请查看：`.kiro/specs/phase1-foundation/tasks.md`

## 获取帮助

- **配置说明**：[CONFIG.md](CONFIG.md)
- **检查清单**：[CHECKLIST.md](CHECKLIST.md)
- **完整文档**：[README.md](README.md)
- **设计文档**：`.kiro/specs/phase1-foundation/design.md`

---

**提示**：首次启动建议按照 CHECKLIST.md 逐项检查，确保环境配置正确。
