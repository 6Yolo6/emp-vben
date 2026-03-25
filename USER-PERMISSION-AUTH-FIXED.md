# 用户直接分配权限认证修复

## 修复时间
2025-10-30

## 问题描述
直接分配给用户的权限没有返回给前端，导致前端显示"权限不足"。

## 问题原因
`SysUserServiceImpl.getUserPermissions()` 方法只包含了：
- ✅ 岗位角色权限
- ✅ 直接分配的角色权限
- ❌ **直接分配的菜单权限（缺失）**

## 解决方案
在 `getUserPermissions()` 方法中添加直接分配菜单权限的查询：

```java
// 6. 获取直接分配给用户的菜单权限
List<Long> directMenuIds = sysUserMenuService.getUserDirectMenuIds(userId);
if (directMenuIds != null && !directMenuIds.isEmpty()) {
    // 查询菜单权限标识
    List<SysMenu> menus = sysMenuMapper.selectListByIds(directMenuIds);
    for (SysMenu menu : menus) {
        if (menu.getPerms() != null && !menu.getPerms().trim().isEmpty()) {
            permissions.add(menu.getPerms());
        }
    }
}
```

## 修复后的权限获取流程

1. **岗位角色权限** - 通过用户岗位关联的角色获得
2. **直接角色权限** - 直接分配给用户的角色权限
3. **直接菜单权限** - 直接分配给用户的菜单权限 ✅ **新增**

所有权限合并到 `Set<String>` 中自动去重，然后返回给前端。

## 测试验证

1. 给用户直接分配权限（通过"分配权限"功能）
2. 重新登录或刷新页面
3. 调用 `/auth/getInfo` 接口
4. 检查返回的 `permissions` 数组是否包含直接分配的权限

## 修改文件
- `rear-emp-platform/emp-system/src/main/java/com/ldjt/emp/service/impl/SysUserServiceImpl.java`

## 相关接口
- `GET /auth/getInfo` - 获取用户信息（包含权限列表）
- `GET /system/user/{id}/permissions` - 获取用户权限详情
- `POST /system/user/{id}/permissions` - 分配直接权限
