# EMP Platform - 企业综合管理平台

## 项目简介

企业综合管理平台（EMP - Enterprise Management Platform）是一个基于 Spring Boot 3.2 + Spring Cloud Alibaba 的微服务架构项目，集成工程管理、财务管理、物资管理等核心业务功能。

## 技术栈

### 后端技术栈

- **Spring Boot**: 3.2.9
- **Spring Cloud**: 2023.0.3
- **Spring Cloud Alibaba**: 2023.0.3.3
- **数据库**: PostgreSQL 15
- **缓存**: Redis 7.0
- **ORM 框架**: MyBatis-Flex 1.11.3
- **权限认证**: Sa-Token 1.44.0
- **工作流引擎**: Flowable 7.0
- **API 文档**: Knife4j 4.5.0
- **服务注册**: Nacos 2.2.3

### 前端技术栈

- **Vue**: 3.x
- **UI 框架**: Vben Admin + Ant Design Vue
- **语言**: TypeScript
- **构建工具**: Vite

## 项目结构

```
emp-platform/
├── emp-common/          # 公共模块 - 通用工具类、常量、枚举
├── emp-framework/       # 框架核心 - 全局异常处理、日志、MyBatis配置
├── emp-gateway/         # 网关服务 - 统一入口、鉴权、限流
├── emp-auth/           # 认证服务 - 登录、Token管理
├── emp-system/         # 系统管理 - 用户、角色、菜单、部门管理
├── emp-workflow/       # 工作流服务 - Flowable流程管理
├── emp-business/       # 业务模块 - 业务功能扩展
└── emp-api/            # API接口定义
```

## 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.8+
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Node.js**: 18+ (前端开发)

### 1. 启动开发环境

#### Windows 系统

```bash
# 双击运行或在命令行执行
init-dev.bat
```

#### Linux/Mac 系统

```bash
# 添加执行权限
chmod +x init-dev.sh

# 运行脚本
./init-dev.sh
```

#### 手动启动

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 2. 服务访问信息

| 服务       | 地址                        | 账号  | 密码      |
| ---------- | --------------------------- | ----- | --------- |
| PostgreSQL | localhost:5432              | emp   | emp123456 |
| Redis      | localhost:6379              | -     | emp123456 |
| Nacos      | http://localhost:8848/nacos | nacos | nacos     |

### 3. 编译项目

```bash
# 进入项目目录
cd rear-emp-platform

# 编译整个项目
mvn clean install

# 跳过测试编译
mvn clean install -DskipTests
```

### 4. 环境检查

```bash
# 运行环境检查脚本
check-env.bat  # Windows
./check-env.sh # Linux/Mac
```

### 5. 配置说明

详细配置说明请参考：[CONFIG.md](CONFIG.md)

**重要提示**：

- 首次启动前，请确保 Docker 环境已启动
- 默认配置中 Nacos 服务发现和配置中心已禁用
- 如需启用 Nacos，请参考 CONFIG.md 中的配置步骤

### 6. 启动服务

```bash
# 启动系统管理服务
cd emp-system
mvn spring-boot:run
```

或使用 IDE 启动 `EmpSystemApplication`

### 7. 访问 API 文档

启动服务后，访问：http://localhost:8080/doc.html

## 开发指南

### 代码规范

- 使用阿里巴巴 Java 开发手册
- 前端使用 ESLint + Prettier
- 强制代码格式化
- 禁止提交 console.log

### Git 分支策略

- **main**: 生产发布分支
- **develop**: 开发集成分支
- **feature/\***: 功能开发分支
- **hotfix/\***: 紧急修复分支

### 提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

## 常用命令

### Docker 相关

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f [服务名]

# 重启服务
docker-compose restart [服务名]

# 进入容器
docker exec -it [容器名] bash
```

### Maven 相关

```bash
# 清理编译
mvn clean

# 编译
mvn compile

# 打包
mvn package

# 安装到本地仓库
mvn install

# 跳过测试
mvn install -DskipTests

# 运行测试
mvn test

# 查看依赖树
mvn dependency:tree
```

## 问题排查

### 1. Docker 服务启动失败

```bash
# 查看详细日志
docker-compose logs [服务名]

# 重新创建容器
docker-compose up -d --force-recreate [服务名]
```

### 2. 端口被占用

```bash
# Windows查看端口占用
netstat -ano | findstr "端口号"

# Linux/Mac查看端口占用
lsof -i :端口号
```

### 3. Maven 依赖下载失败

```bash
# 清理本地仓库
mvn dependency:purge-local-repository

# 强制更新
mvn clean install -U
```

## 项目文档

### 规划文档

- [需求文档](.kiro/specs/phase1-foundation/requirements.md)
- [设计文档](.kiro/specs/phase1-foundation/design.md)
- [任务清单](.kiro/specs/phase1-foundation/tasks.md)
- [项目计划](docs/02-两人全栈开发项目工作计划和排期.md)

### 配置文档

- [配置说明](CONFIG.md) - 详细配置说明
- [快速启动](QUICK-START.md) - 5 分钟快速启动
- [检查清单](CHECKLIST.md) - 配置检查清单
- [依赖说明](DEPENDENCIES.md) - 依赖版本和兼容性
- [问题修复](FIXES.md) - 已知问题和修复方案

## 联系方式

- 项目负责人: [待补充]
- 技术支持: [待补充]

## 许可证

[待补充]

---

**注意**: 本项目仅供学习和开发使用，生产环境部署前请进行充分的安全评估和性能测试。

利德企业综合管理平台（EMP - Enterprise Management Platform）


## 项目文档

### 规划文档
- [需求文档](.kiro/specs/phase1-foundation/requirements.md)
- [设计文档](.kiro/specs/phase1-foundation/design.md)
- [任务清单](.kiro/specs/phase1-foundation/tasks.md)

### 配置文档
- [配置说明](CONFIG.md) - 详细配置说明
- [快速启动](QUICK-START.md) - 5分钟快速启动
- [检查清单](CHECKLIST.md) - 配置检查清单
- [依赖说明](DEPENDENCIES.md) - 依赖版本和兼容性
- [问题修复](FIXES.md) - 已知问题和修复方案

### 开发文档
- [API 注解指南](API-ANNOTATIONS.md) - API 文档注解详细说明
- [注解快速参考](ANNOTATION-QUICK-REF.md) - 常用注解速查表

---

**EMP Platform** - 企业综合管理平台
