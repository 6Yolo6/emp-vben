# MinIO 对象存储集成说明

## 概述

系统已集成 MinIO 对象存储，支持本地存储和 MinIO 存储两种方式，可通过配置灵活切换。

## MinIO 服务端配置

### 1. 端口说明

- **API 端口**: 9000（用于文件上传下载）
- **控制台端口**: 9001（用于 Web 管理界面）

### 2. 默认账号

- **用户名**: admin
- **密码**: password

### 3. 访问地址

- **API 地址**: http://localhost:9000
- **控制台地址**: http://localhost:9001

## 应用配置

### 1. 依赖配置

已在 `pom.xml` 中添加 MinIO 依赖：

```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.7</version>
</dependency>
```

### 2. 配置文件

在 `application.yml` 或 `application-minio.yml` 中配置：

```yaml
# MinIO配置
minio:
  # MinIO服务地址（使用API端口9000，不是控制台端口9001）
  endpoint: http://localhost:9000
  # 访问密钥
  accessKey: admin
  # 密钥
  secretKey: password
  # 默认存储桶名称
  bucketName: demo

# 文件存储配置
file:
  storage:
    # 存储类型：local=本地存储, minio=MinIO存储
    type: minio
  upload:
    # 文件上传大小限制（字节，默认10MB）
    maxSize: 10485760
    # 允许的文件类型
    allowedTypes: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,rar
```

### 3. 切换存储方式

修改 `file.storage.type` 配置项：

- `local`: 使用本地文件系统存储
- `minio`: 使用 MinIO 对象存储

## 核心类说明

### 1. 存储策略接口

- **FileStorageStrategy**: 文件存储策略接口
  - `upload()`: 上传文件
  - `download()`: 下载文件
  - `delete()`: 删除文件
  - `getFileUrl()`: 获取文件访问 URL
  - `getStorageType()`: 获取存储类型

### 2. 存储策略实现

- **LocalFileStorageStrategy**: 本地存储实现（storageType=0）
  - 条件：`file.storage.type=local` 或未配置时默认启用
  - 文件存储在本地文件系统
- **MinioFileStorageStrategy**: MinIO 存储实现（storageType=1）
  - 条件：`file.storage.type=minio`
  - 文件存储在 MinIO 对象存储

### 3. 配置类

- **MinioConfig**: MinIO 配置类
  - 读取 `minio.*` 配置
  - 创建 `MinioClient` Bean

### 4. 服务类

- **SysFileService**: 文件服务接口
- **SysFileServiceImpl**: 文件服务实现
  - 根据配置自动选择存储策略
  - 支持文件上传、下载、删除
  - 支持图片缩略图生成

## 功能特性

### 1. 自动创建存储桶

首次上传文件时，如果存储桶不存在，会自动创建。

### 2. 预签名 URL

MinIO 存储的文件使用预签名 URL 访问，有效期 7 天。

### 3. 公共读取策略

自动为存储桶设置公共读取策略，允许匿名访问文件。

### 4. 缩略图支持

图片上传时自动生成 200x200 的缩略图。

### 5. 文件类型验证

支持配置允许上传的文件类型，默认支持常见的图片、文档、压缩包格式。

### 6. 文件大小限制

支持配置文件上传大小限制，默认 10MB。

## 数据库字段

`sys_file` 表中的 `storage_type` 字段记录文件的存储类型：

- `0`: 本地存储
- `1`: MinIO 存储
- `2`: 阿里云 OSS（预留）

## 使用示例

### 1. 上传文件

```java
@Autowired
private SysFileService fileService;

// 上传普通文件
FileVO fileVO = fileService.upload(multipartFile);

// 上传图片（自动生成缩略图）
FileVO imageVO = fileService.uploadImage(multipartFile);
```

### 2. 下载文件

```java
// 根据文件ID下载
InputStream inputStream = fileService.download(fileId);
```

### 3. 删除文件

```java
// 删除单个文件
fileService.delete(fileId);

// 批量删除
fileService.deleteBatch(new Long[]{id1, id2, id3});
```

## 注意事项

1. **端口配置**:
   - 配置文件中的 `minio.endpoint` 必须使用 API 端口（9000），不是控制台端口（9001）
2. **存储桶命名**:
   - 存储桶名称只能包含小写字母、数字、短横线
   - 长度在 3-63 个字符之间
3. **网络访问**:
   - 确保应用服务器能够访问 MinIO 服务器的 9000 端口
4. **存储策略切换**:
   - 切换存储策略后，已上传的文件不会自动迁移
   - 建议在项目初期确定存储策略

## 扩展开发

### 添加新的存储策略

1. 实现 `FileStorageStrategy` 接口
2. 添加 `@Component` 和 `@ConditionalOnProperty` 注解
3. 实现 `getStorageType()` 方法返回唯一的类型值
4. 在配置文件中添加相应配置

示例：

```java
@Component
@ConditionalOnProperty(name = "file.storage.type", havingValue = "oss")
public class OssFileStorageStrategy implements FileStorageStrategy {
    @Override
    public int getStorageType() {
        return 2; // 阿里云OSS
    }

    // 实现其他方法...
}
```

## 故障排查

### 1. 连接失败

- 检查 MinIO 服务是否启动
- 检查端口配置是否正确（应该是 9000，不是 9001）
- 检查网络连接和防火墙设置

### 2. 权限错误

- 检查 accessKey 和 secretKey 是否正确
- 检查存储桶策略是否正确设置

### 3. 文件上传失败

- 检查文件大小是否超过限制
- 检查文件类型是否在允许列表中
- 查看应用日志获取详细错误信息
