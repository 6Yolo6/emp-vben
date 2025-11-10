package com.ldjt.emp.service;

import com.ldjt.emp.dto.file.FileQueryDTO;
import com.ldjt.emp.vo.file.FileVO;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 系统文件服务接口
 *
 * @author system
 */
public interface SysFileService {

    /**
     * 分页查询文件
     */
    Page<FileVO> page(FileQueryDTO queryDTO);

    /**
     * 根据ID查询文件
     */
    FileVO getById(Long id);

    /**
     * 上传文件
     */
    FileVO upload(MultipartFile file, Integer storageType) throws Exception;

    /**
     * 上传图片（生成缩略图）
     */
    FileVO uploadImage(MultipartFile file, Integer storageType) throws Exception;

    /**
     * 下载文件
     */
    InputStream download(Long id) throws Exception;

    /**
     * 删除文件
     */
    void delete(Long id) throws Exception;

    /**
     * 批量删除文件
     */
    void deleteBatch(Long[] ids) throws Exception;
}
