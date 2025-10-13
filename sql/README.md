# EMP Platform SQL脚本说明

## 文件说明

### 核心SQL脚本（PostgreSQL）

1. **emp_core_tables.sql** - 核心数据库表结构
   - 包含9张核心表的创建脚本
   - 使用PostgreSQL语法
   - 包含完整的注释和索引

2. **emp_core_data.sql** - 核心数据初始化
   - 初始化基础数据
   - 包含超级管理员账号
   - 包含基础菜单和权限数据

### 其他SQL脚本（MySQL - 仅供参考）

3. **01_erp_framework_tables.sql** - ERP框架表结构（MySQL）
4. **02_erp_framework_data.sql** - ERP框架数据（MySQL）
5. **03_erp_flowable_tables.sql** - Flowable工作流表（MySQL）
6. **04_erp_flowable_data.sql** - Flowable工作流数据（MySQL）
7. **erp_framework_basic.sql** - ERP基础脚本（MySQL）

**注意：** 文件3-7使用MySQL语法，仅供参考。项目使用PostgreSQL数据库。

## 核心表结构

### 1. sys_dept - 部门表
- 支持树形结构
- 包含部门基本信息和负责人
- 支持逻辑删除

### 2. sys_post - 岗位表
- 岗位编码唯一
- 支持岗位排序
- 支持逻辑删除

### 3. sys_user - 用户表
- 用户名唯一
- 关联部门
- 支持逻辑删除
- 记录登录信息

### 4. sys_role - 角色表
- 角色标识唯一
- 支持数据权限范围配置
- 支持逻辑删除

### 5. sys_menu - 菜单表
- 支持树形结构
- 支持三种类型：目录(M)、菜单(C)、按钮(F)
- 支持权限标识配置

### 6. sys_user_role - 用户角色关联表
- 用户和角色的多对多关系
- 支持用户直接分配角色

### 7. sys_user_post - 用户岗位关联表
- 用户和岗位的多对多关系
- 支持用户分配多个岗位

### 8. sys_post_role - 岗位角色关联表
- 岗位和角色的多对多关系
- 实现基于岗位的RBAC权限模型

### 9. sys_role_menu - 角色菜单关联表
- 角色和菜单的多对多关系
- 控制角色的菜单权限

## 权限模型

### 基于岗位的RBAC权限模型

```
用户 ──> 岗位 ──> 角色 ──> 菜单权限
  │                │
  └────────────────┴──> 角色 ──> 菜单权限
     (直接分配)
```

**特点：**
1. 用户可以通过岗位获得角色权限（岗位权限）
2. 用户也可以直接分配角色（直接权限）
3. 最终权限 = 岗位权限 + 直接权限
4. 用户调岗时，权限自动变更
5. 用户离岗时，权限自动回收

### 数据权限范围

- 1: 全部数据权限
- 2: 自定义数据权限
- 3: 本部门数据权限
- 4: 本部门及以下数据权限
- 5: 仅本人数据权限

## 初始化数据

### 默认账号

| 用户名 | 密码 | 角色 | 岗位 | 部门 |
|--------|------|------|------|------|
| admin | admin123 | 超级管理员 | CEO | 立德集团 |
| zhangsan | admin123 | 普通员工 | 开发工程师 | 研发部 |
| lisi | admin123 | 普通员工 | 销售专员 | 销售部 |

### 默认部门

- 立德集团（总公司）
  - 深圳分公司
    - 研发部
    - 销售部
    - 人事部
  - 北京分公司

### 默认岗位

1. CEO - 首席执行官
2. CTO - 首席技术官
3. PM - 项目经理
4. DEV - 开发工程师
5. QA - 测试工程师
6. HR - 人事专员
7. SALES - 销售专员

### 默认角色

1. 超级管理员 (admin) - 拥有所有权限
2. 系统管理员 (system) - 系统管理权限
3. 部门经理 (manager) - 本部门及以下数据权限
4. 普通员工 (employee) - 仅本人数据权限

### 默认菜单

- 系统管理
  - 用户管理
  - 角色管理
  - 菜单管理
  - 部门管理
  - 岗位管理
- 系统监控
- 系统工具

## 使用方法

### 方法1：使用Docker Compose（推荐）

```bash
# 1. 停止并删除现有容器和数据
docker-compose down -v

# 2. 启动服务（会自动执行初始化脚本）
docker-compose up -d postgres

# 3. 查看初始化日志
docker logs emp-postgres
```

### 方法2：手动执行SQL脚本

```bash
# 1. 连接到PostgreSQL
psql -h localhost -p 5432 -U emp -d emp_dev

# 2. 执行表结构脚本
\i sql/emp_core_tables.sql

# 3. 执行数据初始化脚本
\i sql/emp_core_data.sql
```

### 方法3：使用psql命令行

```bash
# 一次性执行所有脚本
psql -h localhost -p 5432 -U emp -d emp_dev -f sql/emp_core_tables.sql
psql -h localhost -p 5432 -U emp -d emp_dev -f sql/emp_core_data.sql
```

## 验证安装

```sql
-- 查看所有表
\dt

-- 查看表结构
\d sys_user

-- 查看初始化数据
SELECT COUNT(*) FROM sys_user;
SELECT COUNT(*) FROM sys_role;
SELECT COUNT(*) FROM sys_menu;
SELECT COUNT(*) FROM sys_dept;
SELECT COUNT(*) FROM sys_post;

-- 验证超级管理员账号
SELECT * FROM sys_user WHERE username = 'admin';
```

## 注意事项

1. **数据库类型：** 项目使用PostgreSQL，不要使用MySQL脚本
2. **字符编码：** 使用UTF8编码
3. **时区设置：** Asia/Shanghai
4. **密码加密：** 使用BCrypt加密，默认密码为admin123
5. **逻辑删除：** deleted字段，0-未删除，1-已删除
6. **状态字段：** status字段，0-停用，1-正常
7. **序列重置：** 初始化数据后会自动重置序列

## 故障排查

### 问题1：表已存在

```sql
-- 删除所有表（谨慎操作！）
DROP TABLE IF EXISTS sys_role_menu CASCADE;
DROP TABLE IF EXISTS sys_post_role CASCADE;
DROP TABLE IF EXISTS sys_user_post CASCADE;
DROP TABLE IF EXISTS sys_user_role CASCADE;
DROP TABLE IF EXISTS sys_menu CASCADE;
DROP TABLE IF EXISTS sys_role CASCADE;
DROP TABLE IF EXISTS sys_user CASCADE;
DROP TABLE IF EXISTS sys_post CASCADE;
DROP TABLE IF EXISTS sys_dept CASCADE;
```

### 问题2：权限不足

```sql
-- 授予权限
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO emp;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO emp;
```

### 问题3：序列不同步

```sql
-- 重置所有序列
SELECT setval('sys_dept_id_seq', (SELECT MAX(id) FROM sys_dept));
SELECT setval('sys_post_id_seq', (SELECT MAX(id) FROM sys_post));
SELECT setval('sys_user_id_seq', (SELECT MAX(id) FROM sys_user));
SELECT setval('sys_role_id_seq', (SELECT MAX(id) FROM sys_role));
SELECT setval('sys_menu_id_seq', (SELECT MAX(id) FROM sys_menu));
```

## 相关文档

- [PostgreSQL官方文档](https://www.postgresql.org/docs/)
- [MyBatis-Flex文档](https://mybatis-flex.com/)
- [Sa-Token文档](https://sa-token.cc/)
- [项目架构文档](../ARCHITECTURE.md)
