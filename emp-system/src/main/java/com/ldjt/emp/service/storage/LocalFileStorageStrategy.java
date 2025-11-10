package com.ldjt.emp.service.storage;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件存储策略
 *
 * @author system
 */
@Slf4j
@Component
public class LocalFileStorageStrategy implements FileStorageStrategy {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.domain:http://localhost:8080}")
    private String domain;

    @Override
    public String upload(MultipartFile file, String filePath) throws Exception {
        // 确保目录存在
        Path targetPath = Paths.get(uploadPath, filePath);
        FileUtil.mkParentDirs(targetPath.toFile());

        // 保存文件
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("文件上传成功：{}", targetPath);
        return getFileUrl(filePath);
    }

    @Override
    public String upload(InputStream inputStream, String filePath, String contentType) throws Exception {
        // 确保目录存在
        Path targetPath = Paths.get(uploadPath, filePath);
        FileUtil.mkParentDirs(targetPath.toFile());

        // 保存文件
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("文件上传成功：{}", targetPath);
        return getFileUrl(filePath);
    }

    @Override
    public InputStream download(String filePath) throws Exception {
        Path targetPath = Paths.get(uploadPath, filePath);
        File file = targetPath.toFile();

        if (!file.exists()) {
            throw new RuntimeException("文件不存在：" + filePath);
        }

        return new FileInputStream(file);
    }

    @Override
    public void delete(String filePath) throws Exception {
        Path targetPath = Paths.get(uploadPath, filePath);
        Files.deleteIfExists(targetPath);
        log.info("文件删除成功：{}", targetPath);
    }

    @Override
    public String getFileUrl(String filePath) {
        // 临时返回路径，实际URL会在保存文件记录后更新
        return filePath;
    }

    @Override
    public int getStorageType() {
        return 0; // 本地存储
    }
}
