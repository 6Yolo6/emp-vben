package com.ldjt.emp.service.storage;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO文件存储策略
 *
 * @author system
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "minio.enabled", havingValue = "true", matchIfMissing = false)
public class MinioFileStorageStrategy implements FileStorageStrategy {

    private final MinioClient minioClient;

    @Value("${minio.bucketName:demo}")
    private String bucketName;
    @Value("${minio.endpoint:http://172.22.67.71:9000}")
//    @Value("${minio.endpoint:http://localhost:9001}")
    private String endpoint;

    @Override
    public String upload(MultipartFile file, String filePath) throws Exception {
        // 确保存储桶存在
        ensureBucketExists();

        // 上传文件
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filePath)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        log.info("文件上传到MinIO成功：bucket={}, object={}", bucketName, filePath);
        return getFileUrl(filePath);
    }

    @Override
    public String upload(InputStream inputStream, String filePath, String contentType) throws Exception {
        // 确保存储桶存在
        ensureBucketExists();

        // 上传文件
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filePath)
                        .stream(inputStream, -1, 10485760) // 10MB part size
                        .contentType(contentType)
                        .build()
        );

        log.info("文件流上传到MinIO成功：bucket={}, object={}", bucketName, filePath);
        return getFileUrl(filePath);
    }

    @Override
    public InputStream download(String filePath) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filePath)
                        .build()
        );
    }

    @Override
    public void delete(String filePath) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(filePath)
                        .build()
        );
        log.info("从MinIO删除文件成功：bucket={}, object={}", bucketName, filePath);
    }

    @Override
    public String getFileUrl(String filePath) {
        try {
            // 生成预签名URL，有效期7天
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(filePath)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            log.error("生成MinIO文件URL失败", e);
            return endpoint + "/" + bucketName + "/" + filePath;
        }
    }

    @Override
    public int getStorageType() {
        return 1; // MinIO存储
    }

    /**
     * 确保存储桶存在，不存在则创建
     */
    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            log.info("创建MinIO存储桶成功：{}", bucketName);

            // 设置存储桶为公共读取（可选）
            String policy = """
                    {
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": {"AWS": ["*"]},
                                "Action": ["s3:GetObject"],
                                "Resource": ["arn:aws:s3:::%s/*"]
                            }
                        ]
                    }
                    """.formatted(bucketName);

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(policy)
                            .build()
            );
        }
    }
}
