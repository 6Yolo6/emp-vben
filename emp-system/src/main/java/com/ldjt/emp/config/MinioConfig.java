package com.ldjt.emp.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO配置类
 *
 * @author system
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * MinIO服务地址
     */
    private String endpoint = "http://172.22.67.71:9000";
//    private String endpoint = "http://localhost:9001";

    /**
     * 访问密钥
     */
    private String accessKey = "admin";

    /**
     * 密钥
     */
    private String secretKey = "miniopassword";
//    private String secretKey = "password";

    /**
     * 默认存储桶名称
     */
    private String bucketName = "demo";

    /**
     * 创建MinioClient Bean
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
