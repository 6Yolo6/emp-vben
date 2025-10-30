# 多租户(Multi-Tenant)功能说明

## 概述

本系统实现了完整的多租户架构,支持一个用户属于多个单位,每个单位的数据完全隔离。

## 核心特性

### 1. 一人多单位
- 用户可以同时属于多个租户(单位/公司/组织)
- 每个用户有一个默认(主要)租户
- 用户可以在不同租户间切换

### 2. 数据完全隔离
- 采用行级隔离(tenant_id)
- MyBatis-Flex自动添加租户过滤条件
- 严格防止跨租户数据访问

### 3. 灵活的租户识别
支持三种识别方式(优先级从高到低):
1. 请求头: `X-Tenant-Code: company1`
2. 子域名: `company1.example.com`
3. 路径: `/tenant/company1/...`

### 4. 独立权限体系
- 用户在不同租户中有不同的部门、岗位和角色
- 权限基于当前租户动态加载

## 技术架构

### 数据库设计

```
sys_tenant (租户表)
├── id
├── tenant_code (租户编码,唯一)
├── tenant_name (租户名称)
├── status (状态)
└── ...

sys_user_tenant (用户租户关联表)
├── user_id
├── tenant_id
├── is_primary (是否主租户)
└── status

业务表 (添加tenant_id字段)
├── sys_dept (部门)
├── sys_post (岗位)
├── sys_role (角色)
└── ...
```

### 核心组件

```
TenantContextHolder (租户上下文)
├── 使用ThreadLocal存储当前租户ID
└── 请求结束自动清理

TenantFilter (租户识别过滤器)
├── 从请求中识别租户
├── 设置租户上下文
└── 验证租户状态

TenantLineHandler (MyBatis租户插件)
├── 自动添加 WHERE tenant_id = ?
├── 忽略特定表(sys_user等)
└── 支持租户ID自动填充
```

## 快速开始

### 1. 数据库初始化

```bash
# 执行多租户改造脚本
psql -h localhost -U postgres -d emp_platform -f sql/emp_multi_tenant.sql
```

### 2. 创建租户

```java
// 通过API创建
POST /system/tenant
{
  "tenantCode": "company1",
  "tenantName": "测试公司",
  "status": 1
}
```

### 3. 使用租户

```bash
# 方式1: 请求头
curl -H "X-Tenant-Code: company1" http://localhost:8080/api/...

# 方式2: 子域名
curl http://company1.example.com/api/...

# 方式3: 路径
curl http://localhost:8080/tenant/company1/api/...
```

## API文档

### 租户管理

| 接口 | 方法 | 说明 |
|------|------|------|
| /system/tenant | POST | 创建租户 |
| /system/tenant/{id} | PUT | 更新租户 |
| /system/tenant/{id} | DELETE | 删除租户 |
| /system/tenant/{id} | GET | 查询租户 |
| /system/tenant/list | GET | 查询所有租户 |
| /system/tenant/page | GET | 分页查询租户 |
| /system/tenant/{id}/status | PUT | 修改租户状态 |

详细API文档请访问: http://localhost:8080/doc.html

## 开发指南

### 1. 创建支持多租户的实体

```java
@Data
@EqualsAndHashCode(callSuper = true)
@Table("your_table")
public class YourEntity extends TenantBaseEntity {
    // 字段定义
}
```

### 2. 获取当前租户ID

```java
// 方式1: 获取租户ID(未设置会抛异常)
Long tenantId = TenantContextHolder.getTenantId();

// 方式2: 获取租户ID(未设置返回null)
Long tenantId = TenantContextHolder.getTenantIdOrNull();
```

### 3. 临时忽略租户隔离

```java
// 使用MyBatis-Flex的忽略租户功能
TenantManager.withoutTenantCondition(() -> {
    // 这里的查询不会添加租户过滤条件
    List<SysDept> allDepts = deptService.list();
    return allDepts;
});
```

## 配置说明

### application.yml

```yaml
# 租户配置(未来扩展)
tenant:
  enabled: true
  identify-type: subdomain  # subdomain, path, header
```

## 测试

### 单元测试

```java
@Test
public void testDataIsolation() {
    // 设置租户1
    TenantContextHolder.setTenantId(1L);
    deptService.save(dept1);
    
    // 设置租户2
    TenantContextHolder.setTenantId(2L);
    deptService.save(dept2);
    
    // 验证隔离
    TenantContextHolder.setTenantId(1L);
    List<SysDept> depts = deptService.list();
    // 只能查到租户1的数据
}
```

### 集成测试

参考: `MULTI-TENANT-TESTING-GUIDE.md`

## 性能优化

### 1. 索引优化
所有tenant_id字段都已建立索引:
```sql
CREATE INDEX idx_dept_tenant_id ON sys_dept(tenant_id);
CREATE INDEX idx_post_tenant_id ON sys_post(tenant_id);
CREATE INDEX idx_role_tenant_id ON sys_role(tenant_id);
```

### 2. 缓存策略
租户信息使用Spring Cache缓存:
```java
@Cacheable(value = "tenant", key = "#tenantCode")
public SysTenant getByCode(String tenantCode)
```

## 安全注意事项

### 1. 数据隔离
- ✅ 所有查询自动添加tenant_id过滤
- ✅ 跨租户访问被阻止
- ✅ 租户上下文请求结束自动清理

### 2. 权限控制
- ✅ 基于当前租户验证权限
- ✅ 超级管理员可跨租户访问
- ✅ 操作日志记录租户信息

### 3. 审计日志
所有操作都记录租户信息,便于审计追溯

## 常见问题

### Q1: 如何添加新的业务表支持多租户?

1. 添加tenant_id字段
```sql
ALTER TABLE your_table ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 0;
CREATE INDEX idx_your_table_tenant_id ON your_table(tenant_id);
```

2. 实体类继承TenantBaseEntity
```java
public class YourEntity extends TenantBaseEntity { }
```

3. 迁移现有数据
```sql
UPDATE your_table SET tenant_id = 1 WHERE tenant_id = 0;
```

### Q2: 如何查询所有租户的数据?

使用忽略租户条件:
```java
TenantManager.withoutTenantCondition(() -> {
    return yourService.list();
});
```

### Q3: 租户识别失败怎么办?

检查:
1. TenantFilter是否正确注册
2. 请求头/域名/路径格式是否正确
3. 租户是否存在且状态正常

## 相关文档

- [架构设计](MULTI-TENANT-ARCHITECTURE.md)
- [实施计划](MULTI-TENANT-IMPLEMENTATION-PLAN.md)
- [开发清单](MULTI-TENANT-DEV-CHECKLIST.md)
- [快速开始](MULTI-TENANT-QUICK-START.md)
- [测试指南](MULTI-TENANT-TESTING-GUIDE.md)
- [完成总结](MULTI-TENANT-PHASE1-COMPLETED.md)

## 版本历史

### v1.0 (2025-10-22)
- ✅ 基础架构实现
- ✅ 租户识别和数据隔离
- ✅ 租户管理功能
- ⏳ 多租户登录认证(待开发)
- ⏳ 前端适配(待开发)

## 技术支持

如有问题,请联系开发团队或查看相关文档。

---

**开发团队**: EMP Team  
**文档版本**: v1.0  
**最后更新**: 2025-10-22
