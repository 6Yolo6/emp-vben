# EMP Auth 认证服务

## 模块说明

emp-auth模块是认证辅助服务，主要提供验证码生成和验证功能。

**注意：** 用户登录、登出、Token刷新等核心认证功能在 `emp-system` 模块中实现。

## 功能列表

### 验证码服务

- **验证码生成**：生成图形验证码，返回Base64编码的图片和UUID
- **验证码验证**：验证用户输入的验证码是否正确
- **验证码存储**：验证码存储在Redis中，有效期2分钟

## API接口

### 获取验证码

```http
GET /auth/captcha
```

**响应示例：**
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

## 配置说明

### 验证码配置

在 `CaptchaConfig` 中可以配置：
- 图片宽度：120px
- 图片高度：40px
- 验证码长度：4位
- 字体：宋体、楷体、微软雅黑
- 有效期：2分钟

### Redis配置

验证码存储在Redis中，key格式：`captcha:code:{uuid}`

## 依赖关系

- 依赖 `emp-system` 模块（用于访问用户服务）
- 使用 `kaptcha` 生成验证码图片
- 使用 `RedisCache` 存储验证码

## 使用示例

### 前端获取验证码

```javascript
// 1. 获取验证码
const response = await axios.get('/auth/captcha');
const { uuid, img } = response.data.data;

// 2. 显示验证码图片
document.getElementById('captcha-img').src = `data:image/jpeg;base64,${img}`;

// 3. 登录时携带uuid和用户输入的验证码
await axios.post('/system/auth/login', {
  username: 'admin',
  password: 'admin123',
  code: '1234',  // 用户输入的验证码
  uuid: uuid     // 验证码UUID
});
```

## 启动说明

```bash
# 开发环境启动
mvn spring-boot:run

# 或使用IDE直接运行 AuthApplication
```

服务默认端口：8081

## 注意事项

1. 验证码有效期为2分钟，过期后需要重新获取
2. 验证码验证后会自动删除，不能重复使用
3. 验证码不区分大小写
4. 登录功能在 `emp-system` 模块的 `AuthServiceImpl` 中实现
