package com.ldjt.emp.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件存储策略接口
 *
 * @author system
 */
public interface FileStorageStrategy {

    /**
     * 上传文件
     *
     * @param file     文件
     * @param filePath 文件路径
     * @return 文件访问URL
     */
    String upload(MultipartFile file, String filePath) throws Exception;

    /**
     * 上传文件流
     *
     * @param inputStream 文件流
     * @param filePath    文件路径
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    String upload(InputStream inputStream, String filePath, String contentType) throws Exception;

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @return 文件流
     */
    InputStream download(String filePath) throws Exception;

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    void delete(String filePath) throws Exception;

    /**
     * 获取文件访问URL
     *
     * @param filePath 文件路径
     * @return 文件访问URL
     */
    String getFileUrl(String filePath);

    /**
     * 获取存储类型
     *
     * @return 存储类型：0=本地存储 1=MinIO
     */
    int getStorageType();
}
