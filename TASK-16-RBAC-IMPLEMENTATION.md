# 任务 16：基于岗位的 RBAC 权限模型实现

## 概述

实现基于岗位的 RBAC（Role-Based Access Control）权限模型，支持：
- 岗位-角色-权限的层级结构
- 用户可以通过岗位获得权限（岗位权限）
- 用户也可以直接分配角色获得权限（直接权限）
- 人员调岗时权限自动变更
- 数据权限过滤

## 权限模型设计

### 权限层级结构

```
用户 (User)
  ├─ 岗位 (Post) ──→ 角色 (Role) ──→ 菜单/权限 (Menu/Permission)
  └─ 直接角色 (Direct Role) ──→ 菜单/权限 (Menu/Permission)
```

### 数据库表关系

```
sys_user (用户表)
  ├─ sys_user_post (用户岗位关联表) ──→ sys_post (岗位表)
  │                                      └─ sys_post_role (岗位角色关联表) ──→ sys_role (角色表)
  └─ sys_user_role (用户角色关联表) ──→ sys_role (角色表)
                                          └─ sys_role_menu (角色菜单关联表) ──→ sys_menu (菜单表)
```

## 实现计划

### 阶段 1：角色管理基础功能

#### 1.1 角色 CRUD 接口
- [x] 创建角色（已有基础实现）
- [x] 查询角色列表
- [x] 更新角色
- [x] 删除角色

#### 1.2 角色权限分配
- [ ] 为角色分配菜单权限
- [ ] 查询角色的菜单权限
- [ ] 设置角色的数据权限范围

### 阶段 2：权限加载和缓存

#### 2.1 用户登录时加载权限
- [ ] 加载用户的岗位角色权限
- [ ] 加载用户的直接角色权限
- [ ] 合并权限并存入 Redis 缓存
- [ ] 实现权限缓存 Key 设计

#### 2.2 权限缓存管理
- [ ] 实现权限缓存工具类
- [ ] 实现权限刷新机制
- [ ] 实现权限清除机制

### 阶段 3：接口权限验证

#### 3.1 Sa-Token 权限验证
- [ ] 配置 Sa-Token 权限验证
- [ ] 实现 StpInterface 接口
- [ ] 在 Controller 方法上添加权限注解

#### 3.2 权限注解使用
```java
@SaCheckPermission("system:user:add")
@SaCheckRole("admin")
```

### 阶段 4：数据权限过滤

#### 4.1 数据权限类型
1. **全部数据权限** - 可以查看所有数据
2. **本部门数据权限** - 只能查看本部门数据
3. **本部门及以下数据权限** - 可以查看本部门及子部门数据
4. **仅本人数据权限** - 只能查看自己的数据
5. **自定义数据权限** - 指定可以查看的部门

#### 4.2 数据权限实现
- [ ] 创建 `@DataScope` 注解
- [ ] 创建数据权限 AOP 切面
- [ ] 实现 SQL 过滤条件注入
- [ ] 实现基于部门的数据过滤

### 阶段 5：权限变更处理

#### 5.1 用户调岗处理
- [ ] 更新用户岗位关联
- [ ] 自动刷新用户权限缓存
- [ ] 记录调岗日志

#### 5.2 用户离岗处理
- [ ] 清除用户岗位关联
- [ ] 自动回收岗位权限
- [ ] 保留直接分配的角色权限

#### 5.3 岗位角色变更处理
- [ ] 清除该岗位下所有用户的权限缓存
- [ ] 用户下次访问时重新加载权限

#### 5.4 角色权限变更处理
- [ ] 清除相关用户的权限缓存
- [ ] 用户下次访问时重新加载权限

### 阶段 6：删除前关联检查

#### 6.1 岗位删除检查
- [x] 检查是否有用户关联该岗位（已实现）
- [ ] 检查是否有角色关联该岗位

#### 6.2 角色删除检查
- [ ] 检查是否有岗位关联该角色
- [ ] 检查是否有用户直接关联该角色

## 详细设计

### 1. 权限缓存设计

#### 缓存 Key 设计
```
# 用户权限列表
permission:user:{userId}:permissions

# 用户角色列表
permission:user:{userId}:roles

# 用户数据权限范围
permission:user:{userId}:dataScope
```

#### 缓存数据结构
```json
{
  "permissions": ["system:user:add", "system:user:edit", "system:user:delete"],
  "roles": ["admin", "user"],
  "dataScope": {
    "type": "DEPT_AND_CHILD",
    "deptIds": [1, 2, 3]
  }
}
```

### 2. StpInterface 实现

```java
@Component
public class StpInterfaceImpl implements StpInterface {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 从 Redis 缓存中获取用户权限列表
        String key = "permission:user:" + loginId + ":permissions";
        List<String> permissions = (List<String>) redisTemplate.opsForValue().get(key);
        
        if (permissions == null) {
            // 缓存不存在，重新加载
            permissions = loadUserPermissions(Long.valueOf(loginId.toString()));
            redisTemplate.opsForValue().set(key, permissions, 2, TimeUnit.HOURS);
        }
        
        return permissions;
    }
    
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 从 Redis 缓存中获取用户角色列表
        String key = "permission:user:" + loginId + ":roles";
        List<String> roles = (List<String>) redisTemplate.opsForValue().get(key);
        
        if (roles == null) {
            // 缓存不存在，重新加载
            roles = loadUserRoles(Long.valueOf(loginId.toString()));
            redisTemplate.opsForValue().set(key, roles, 2, TimeUnit.HOURS);
        }
        
        return roles;
    }
    
    private List<String> loadUserPermissions(Long userId) {
        // 1. 查询用户的岗位
        // 2. 查询岗位的角色
        // 3. 查询角色的权限
        // 4. 查询用户直接分配的角色
        // 5. 查询直接角色的权限
        // 6. 合并去重
    }
    
    private List<String> loadUserRoles(Long userId) {
        // 1. 查询用户的岗位角色
        // 2. 查询用户直接分配的角色
        // 3. 合并去重
    }
}
```

### 3. 数据权限注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    
    /**
     * 部门表的别名
     */
    String deptAlias() default "d";
    
    /**
     * 用户表的别名
     */
    String userAlias() default "u";
    
    /**
     * 权限字符串（用于多个数据权限）
     */
    String permission() default "";
}
```

### 4. 数据权限 AOP 切面

```java
@Aspect
@Component
@Slf4j
public class DataScopeAspect {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Around("@annotation(dataScope)")
    public Object around(ProceedingJoinPoint point, DataScope dataScope) throws Throwable {
        // 1. 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 2. 从缓存获取用户的数据权限范围
        String key = "permission:user:" + userId + ":dataScope";
        DataScopeDTO dataScopeDTO = (DataScopeDTO) redisTemplate.opsForValue().get(key);
        
        if (dataScopeDTO == null) {
            // 缓存不存在，重新加载
            dataScopeDTO = loadUserDataScope(userId);
            redisTemplate.opsForValue().set(key, dataScopeDTO, 2, TimeUnit.HOURS);
        }
        
        // 3. 根据数据权限类型构建 SQL 过滤条件
        String sqlFilter = buildSqlFilter(dataScopeDTO, dataScope);
        
        // 4. 将过滤条件存入 ThreadLocal
        DataScopeContextHolder.setSqlFilter(sqlFilter);
        
        try {
            // 5. 执行原方法
            return point.proceed();
        } finally {
            // 6. 清除 ThreadLocal
            DataScopeContextHolder.clear();
        }
    }
    
    private String buildSqlFilter(DataScopeDTO dataScope, DataScope annotation) {
        String deptAlias = annotation.deptAlias();
        String userAlias = annotation.userAlias();
        
        switch (dataScope.getType()) {
            case ALL:
                // 全部数据权限，不添加过滤条件
                return "";
                
            case DEPT:
                // 本部门数据权限
                return String.format("%s.dept_id = %d", deptAlias, dataScope.getDeptId());
                
            case DEPT_AND_CHILD:
                // 本部门及以下数据权限
                List<Long> deptIds = dataScope.getDeptIds();
                return String.format("%s.dept_id IN (%s)", deptAlias, 
                    deptIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
                
            case SELF:
                // 仅本人数据权限
                return String.format("%s.create_by = %d", userAlias, dataScope.getUserId());
                
            case CUSTOM:
                // 自定义数据权限
                List<Long> customDeptIds = dataScope.getCustomDeptIds();
                return String.format("%s.dept_id IN (%s)", deptAlias,
                    customDeptIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
                
            default:
                return "";
        }
    }
}
```

### 5. 权限刷新机制

```java
@Service
public class PermissionService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 刷新用户权限缓存
     */
    public void refreshUserPermission(Long userId) {
        String permissionKey = "permission:user:" + userId + ":permissions";
        String roleKey = "permission:user:" + userId + ":roles";
        String dataScopeKey = "permission:user:" + userId + ":dataScope";
        
        // 删除缓存
        redisTemplate.delete(permissionKey);
        redisTemplate.delete(roleKey);
        redisTemplate.delete(dataScopeKey);
        
        // 重新加载
        loadUserPermissions(userId);
        loadUserRoles(userId);
        loadUserDataScope(userId);
    }
    
    /**
     * 刷新岗位下所有用户的权限缓存
     */
    public void refreshPostUsersPermission(Long postId) {
        // 1. 查询该岗位下的所有用户
        List<Long> userIds = getUserIdsByPostId(postId);
        
        // 2. 刷新每个用户的权限缓存
        userIds.forEach(this::refreshUserPermission);
    }
    
    /**
     * 刷新角色相关用户的权限缓存
     */
    public void refreshRoleUsersPermission(Long roleId) {
        // 1. 查询直接分配该角色的用户
        List<Long> directUserIds = getUserIdsByRoleId(roleId);
        
        // 2. 查询通过岗位拥有该角色的用户
        List<Long> postUserIds = getUserIdsByPostRoleId(roleId);
        
        // 3. 合并去重
        Set<Long> allUserIds = new HashSet<>();
        allUserIds.addAll(directUserIds);
        allUserIds.addAll(postUserIds);
        
        // 4. 刷新每个用户的权限缓存
        allUserIds.forEach(this::refreshUserPermission);
    }
}
```

## API 接口设计

### 1. 角色权限管理

```
# 为角色分配菜单权限
POST /system/role/{roleId}/menus
Request: { menuIds: [1, 2, 3] }

# 查询角色的菜单权限
GET /system/role/{roleId}/menus
Response: [1, 2, 3]

# 设置角色的数据权限
PUT /system/role/{roleId}/dataScope
Request: { 
  type: "DEPT_AND_CHILD",
  deptIds: [1, 2, 3]
}
```

### 2. 用户岗位管理

```
# 为用户分配岗位
POST /system/user/{userId}/posts
Request: { postIds: [1, 2] }

# 查询用户的岗位
GET /system/user/{userId}/posts
Response: [...]

# 用户调岗
PUT /system/user/{userId}/changePost
Request: { 
  oldPostId: 1,
  newPostId: 2
}

# 用户离岗
DELETE /system/user/{userId}/post/{postId}
```

### 3. 权限查询

```
# 查询用户权限
GET /system/permission/user/{userId}
Response: {
  permissions: ["system:user:add", ...],
  roles: ["admin", ...],
  dataScope: {...}
}

# 刷新用户权限
POST /system/permission/user/{userId}/refresh
```

## 测试用例

### 1. 权限加载测试
- 测试用户登录时权限加载
- 测试岗位权限和直接权限合并
- 测试权限缓存

### 2. 接口权限验证测试
- 测试有权限访问接口
- 测试无权限访问接口
- 测试权限注解

### 3. 数据权限过滤测试
- 测试全部数据权限
- 测试本部门数据权限
- 测试本部门及以下数据权限
- 测试仅本人数据权限
- 测试自定义数据权限

### 4. 权限变更测试
- 测试用户调岗后权限变更
- 测试用户离岗后权限回收
- 测试岗位角色变更后权限刷新
- 测试角色权限变更后权限刷新

### 5. 删除检查测试
- 测试删除有用户的岗位
- 测试删除有关联的角色

## 相关文件

### 后端文件
- `rear-emp-platform/emp-system/src/main/java/com/ldjt/emp/service/PermissionService.java`
- `rear-emp-platform/emp-system/src/main/java/com/ldjt/emp/config/StpInterfaceImpl.java`
- `rear-emp-platform/emp-framework/src/main/java/com/ldjt/emp/framework/aspect/DataScopeAspect.java`
- `rear-emp-platform/emp-framework/src/main/java/com/ldjt/emp/framework/annotation/DataScope.java`

### 数据库表
- `sys_role` - 角色表
- `sys_menu` - 菜单表
- `sys_role_menu` - 角色菜单关联表
- `sys_user_role` - 用户角色关联表
- `sys_post_role` - 岗位角色关联表
- `sys_user_post` - 用户岗位关联表

## 注意事项

1. **权限缓存过期时间**：建议设置 2 小时，避免权限变更不及时生效
2. **权限刷新时机**：在权限变更时主动刷新，避免等待缓存过期
3. **数据权限性能**：数据权限过滤会影响查询性能，需要合理使用索引
4. **并发安全**：权限加载和刷新需要考虑并发情况
5. **权限粒度**：权限粒度不宜过细，避免管理复杂度过高

---

创建时间：2025-10-15
