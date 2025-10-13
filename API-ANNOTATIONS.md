# API 文档注解使用指南

## Knife4j 注解说明

Knife4j 使用标准的 **OpenAPI 3.0 注解**（来自 `io.swagger.v3.oas.annotations` 包），与 SpringDoc 完全兼容。

**重要**：从 SpringDoc 迁移到 Knife4j **不需要修改任何注解**！

## 常用注解

### 1. Controller 类注解

#### @Tag
用于标注 Controller 类，定义接口分组。

```java
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    // ...
}
```

**属性说明**：
- `name`: 分组名称（必填）
- `description`: 分组描述（可选）

### 2. 方法注解

#### @Operation
用于标注 Controller 方法，描述接口功能。

```java
import io.swagger.v3.oas.annotations.Operation;

@GetMapping("/{id}")
@Operation(summary = "查询用户", description = "根据ID查询用户详细信息")
public Result<User> getUser(@PathVariable Long id) {
    // ...
}
```

**属性说明**：
- `summary`: 接口简要说明（必填）
- `description`: 接口详细说明（可选）
- `tags`: 所属分组（可选，默认使用类上的 @Tag）

### 3. 参数注解

#### @Parameter
用于标注方法参数，描述参数信息。

```java
import io.swagger.v3.oas.annotations.Parameter;

@GetMapping("/{id}")
public Result<User> getUser(
    @Parameter(description = "用户ID", required = true, example = "1")
    @PathVariable Long id) {
    // ...
}
```

**属性说明**：
- `description`: 参数描述（必填）
- `required`: 是否必填（可选，默认 false）
- `example`: 示例值（可选）
- `hidden`: 是否隐藏（可选，默认 false）

#### @Parameters
用于标注多个参数。

```java
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;

@GetMapping("/list")
@Parameters({
    @Parameter(name = "page", description = "页码", example = "1"),
    @Parameter(name = "size", description = "每页数量", example = "10")
})
public Result<Page<User>> list(@RequestParam Integer page, @RequestParam Integer size) {
    // ...
}
```

### 4. 实体类注解

#### @Schema
用于标注实体类和字段，描述数据模型。

**类级别**：
```java
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "用户信息")
public class User {
    // ...
}
```

**字段级别**：
```java
@Data
@Schema(description = "用户信息")
public class User {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", required = true, example = "admin")
    private String username;
    
    @Schema(description = "昵称", example = "管理员")
    private String nickname;
    
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;
    
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;
}
```

**属性说明**：
- `description`: 字段描述（必填）
- `required`: 是否必填（可选）
- `example`: 示例值（可选）
- `defaultValue`: 默认值（可选）
- `allowableValues`: 允许的值（可选）
- `hidden`: 是否隐藏（可选）
- `accessMode`: 访问模式（READ_ONLY, WRITE_ONLY, READ_WRITE）

### 5. 请求体注解

#### @RequestBody + @Schema
用于标注请求体参数。

```java
@PostMapping
@Operation(summary = "创建用户")
public Result<Void> create(
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "用户信息",
        required = true
    )
    @RequestBody UserDTO dto) {
    // ...
}
```

### 6. 响应注解

#### @ApiResponse
用于标注响应信息。

```java
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@GetMapping("/{id}")
@Operation(summary = "查询用户")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "查询成功"),
    @ApiResponse(responseCode = "404", description = "用户不存在"),
    @ApiResponse(responseCode = "500", description = "服务器错误")
})
public Result<User> getUser(@PathVariable Long id) {
    // ...
}
```

## 完整示例

### Controller 示例

```java
package com.ldjt.emp.controller;

import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.domain.User;
import com.ldjt.emp.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理 Controller
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    
    /**
     * 查询用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户列表", description = "分页查询用户列表")
    public Result<Page<User>> list(
        @Parameter(description = "页码", example = "1")
        @RequestParam(defaultValue = "1") Integer page,
        
        @Parameter(description = "每页数量", example = "10")
        @RequestParam(defaultValue = "10") Integer size) {
        // ...
        return Result.success(userList);
    }
    
    /**
     * 查询用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情", description = "根据ID查询用户详细信息")
    public Result<User> getById(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathVariable Long id) {
        // ...
        return Result.success(user);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public Result<Void> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "用户信息",
            required = true
        )
        @RequestBody UserDTO dto) {
        // ...
        return Result.success();
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户信息")
    public Result<Void> update(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathVariable Long id,
        
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "用户信息",
            required = true
        )
        @RequestBody UserDTO dto) {
        // ...
        return Result.success();
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public Result<Void> delete(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathVariable Long id) {
        // ...
        return Result.success();
    }
}
```

### 实体类示例

```java
package com.ldjt.emp.domain;

import com.ldjt.emp.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户信息")
public class User extends BaseEntity {
    
    @Schema(description = "用户名", required = true, example = "admin")
    private String username;
    
    @Schema(description = "密码", required = true, example = "123456", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
    
    @Schema(description = "昵称", example = "管理员")
    private String nickname;
    
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;
    
    @Schema(description = "手机号", example = "13800138000")
    private String mobile;
    
    @Schema(description = "头像", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;
    
    @Schema(description = "部门ID", example = "1")
    private Long deptId;
}
```

### DTO 示例

```java
package com.ldjt.emp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 用户 DTO
 */
@Data
@Schema(description = "用户数据传输对象")
public class UserDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", required = true, example = "admin")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", required = true, example = "123456")
    private String password;
    
    @Schema(description = "昵称", example = "管理员")
    private String nickname;
    
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String mobile;
}
```

## 注解导入说明

### 正确的导入语句

```java
// Controller 注解
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

// 请求体注解
import io.swagger.v3.oas.annotations.parameters.RequestBody;

// 响应注解
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

// 实体类注解
import io.swagger.v3.oas.annotations.media.Schema;
```

### ❌ 错误的导入（旧版 Swagger 2.x）

```java
// 不要使用这些旧版注解！
import io.swagger.annotations.Api;           // ❌
import io.swagger.annotations.ApiOperation;  // ❌
import io.swagger.annotations.ApiParam;      // ❌
import io.swagger.annotations.ApiModel;      // ❌
```

## 注解迁移对照表

如果你之前使用 Swagger 2.x，可以参考以下对照表：

| Swagger 2.x | OpenAPI 3.0 (Knife4j) |
|-------------|----------------------|
| `@Api` | `@Tag` |
| `@ApiOperation` | `@Operation` |
| `@ApiParam` | `@Parameter` |
| `@ApiModel` | `@Schema` (类级别) |
| `@ApiModelProperty` | `@Schema` (字段级别) |
| `@ApiImplicitParam` | `@Parameter` |
| `@ApiImplicitParams` | `@Parameters` |
| `@ApiResponse` | `@ApiResponse` (相同) |
| `@ApiResponses` | `@ApiResponses` (相同) |

## 最佳实践

### 1. 必填注解

- Controller 类：`@Tag`
- Controller 方法：`@Operation`
- 实体类：`@Schema`（类和字段）

### 2. 推荐注解

- 方法参数：`@Parameter`（提供示例值）
- 请求体：`@RequestBody`（OpenAPI 的）
- 响应：`@ApiResponse`（描述不同状态码）

### 3. 注解顺序

```java
@GetMapping("/{id}")
@Operation(summary = "查询用户")
@ApiResponse(responseCode = "200", description = "成功")
public Result<User> getUser(
    @Parameter(description = "用户ID", required = true)
    @PathVariable Long id) {
    // ...
}
```

### 4. 示例值

为所有字段提供示例值，方便在线调试：

```java
@Schema(description = "用户名", example = "admin")
private String username;
```

### 5. 隐藏敏感字段

```java
@Schema(description = "密码", accessMode = Schema.AccessMode.WRITE_ONLY)
private String password;
```

## 常见问题

### Q1: 注解不生效？

**检查**：
1. 是否正确导入 `io.swagger.v3.oas.annotations` 包
2. 是否在 Controller 类上添加了 `@Tag` 注解
3. Knife4j 配置是否启用：`knife4j.enable=true`

### Q2: 文档中文乱码？

**解决**：
```yaml
knife4j:
  setting:
    language: zh_cn
```

### Q3: 需要修改现有注解吗？

**答案**：不需要！Knife4j 使用标准的 OpenAPI 3.0 注解，与 SpringDoc 完全兼容。

## 参考资源

- [OpenAPI 3.0 规范](https://swagger.io/specification/)
- [Knife4j 官方文档](https://doc.xiaominfo.com/)
- [Swagger 注解文档](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)

---

**总结**：使用 Knife4j 不需要修改任何 `@Schema` 等注解，它们都是标准的 OpenAPI 3.0 注解！
