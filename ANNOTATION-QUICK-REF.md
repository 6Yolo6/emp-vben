# API 注解快速参考

## ✅ 不需要修改注解！

**重要**：从 SpringDoc 迁移到 Knife4j **不需要修改任何注解**！

Knife4j 使用标准的 OpenAPI 3.0 注解，与 SpringDoc 完全兼容。

## 常用注解速查

### Controller 类

```java
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "用户管理", description = "用户相关接口")
```

### Controller 方法

```java
import io.swagger.v3.oas.annotations.Operation;

@Operation(summary = "查询用户", description = "根据ID查询用户信息")
```

### 方法参数

```java
import io.swagger.v3.oas.annotations.Parameter;

@Parameter(description = "用户ID", required = true, example = "1")
```

### 实体类

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户信息")  // 类级别
```

### 实体字段

```java
@Schema(description = "用户名", required = true, example = "admin")
private String username;
```

## 完整示例

```java
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理")
public class UserController {

    @GetMapping("/{id}")
    @Operation(summary = "查询用户")
    public Result<User> getUser(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long id) {
        return Result.success(user);
    }
}

@Data
@Schema(description = "用户信息")
public class User extends BaseEntity {

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "状态", example = "1")
    private Integer status;
}
```

## 注解包路径

```java
// ✅ 正确的导入（OpenAPI 3.0）
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

// ❌ 错误的导入（Swagger 2.x - 不要使用）
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
```

## 访问文档

启动应用后访问：http://localhost:8080/doc.html

---

详细说明请查看：[API-ANNOTATIONS.md](API-ANNOTATIONS.md)
