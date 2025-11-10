package com.ldjt.emp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.file.FileQueryDTO;
import com.ldjt.emp.entity.SysFile;
import com.ldjt.emp.mapper.SysFileMapper;
import com.ldjt.emp.service.SysFileService;
import com.ldjt.emp.service.storage.FileStorageStrategy;
import com.ldjt.emp.vo.file.FileVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ldjt.emp.entity.table.SysFileTableDef.SYS_FILE;

/**
 * 系统文件服务实现
 *
 * @author system
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl implements SysFileService {

    private final SysFileMapper fileMapper;
    private final List<FileStorageStrategy> fileStorageStrategies;

    @Value("${file.storage.type:local}") // 默认本地存储
    private String storageType;

    @Value("${file.upload.maxSize:10485760}") // 默认10MB
    private long maxFileSize;

    @Value("${file.upload.allowedTypes:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,rar}")
    private String allowedTypes;

    /**
     * 获取当前配置的存储策略
     */
    private FileStorageStrategy getFileStorageStrategy() {
        int type = "minio".equalsIgnoreCase(storageType) ? 1 : 0;
        return fileStorageStrategies.stream()
                .filter(strategy -> strategy.getStorageType() == type)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到存储策略：" + storageType));
    }

    /**
     * 根据指定类型获取存储策略
     */
    private FileStorageStrategy getFileStorageStrategy(Integer type) {
        if (type == null) {
            return getFileStorageStrategy();
        }
        return fileStorageStrategies.stream()
                .filter(strategy -> strategy.getStorageType() == type)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到存储策略：" + type));
    }

    @Override
    public Page<FileVO> page(FileQueryDTO queryDTO) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(SYS_FILE)
                .where(SYS_FILE.FILE_NAME.like(queryDTO.getFileName(), StrUtil::isNotBlank))
                .and(SYS_FILE.ORIGINAL_NAME.like(queryDTO.getOriginalName(), StrUtil::isNotBlank))
                .and(SYS_FILE.FILE_TYPE.eq(queryDTO.getFileType(), StrUtil::isNotBlank))
                .and(SYS_FILE.STORAGE_TYPE.eq(queryDTO.getStorageType(), queryDTO.getStorageType() != null))
                .orderBy(SYS_FILE.CREATE_TIME.desc());

        Page<SysFile> page = fileMapper.paginate(
                new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()),
                queryWrapper
        );

        List<FileVO> voList = BeanUtil.copyToList(page.getRecords(), FileVO.class);
        
        Page<FileVO> voPage = new Page<>();
        voPage.setPageNumber(page.getPageNumber());
        voPage.setPageSize(page.getPageSize());
        voPage.setTotalRow(page.getTotalRow());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public FileVO getById(Long id) {
        SysFile file = fileMapper.selectOneById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        return BeanUtil.copyProperties(file, FileVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO upload(MultipartFile file, Integer storageType) throws Exception {
        // 验证文件
        validateFile(file);

        // 生成文件信息
        String originalName = file.getOriginalFilename();
        String fileExt = FileUtil.extName(originalName);
        String fileName = IdUtil.fastSimpleUUID() + "." + fileExt;
        String filePath = generateFilePath(fileName);

        // 计算MD5
        String md5 = DigestUtil.md5Hex(file.getInputStream());

        // 获取存储策略并上传文件
        FileStorageStrategy strategy = getFileStorageStrategy(storageType);
        String fileUrl = strategy.upload(file, filePath);

        // 保存文件记录
        SysFile sysFile = new SysFile();
        sysFile.setFileName(fileName);
        sysFile.setOriginalName(originalName);
        sysFile.setFilePath(filePath);
        sysFile.setFileSize(file.getSize());
        sysFile.setFileType(file.getContentType());
        sysFile.setFileExt(fileExt);
        sysFile.setStorageType(strategy.getStorageType());
        sysFile.setMd5(md5);
        sysFile.setStatus(1);

        fileMapper.insert(sysFile);

        // 生成正确的文件访问URL（使用文件ID）
        String correctFileUrl = "/api/system/file/download/" + sysFile.getId();
        sysFile.setFileUrl(correctFileUrl);
        fileMapper.update(sysFile);

        return BeanUtil.copyProperties(sysFile, FileVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO uploadImage(MultipartFile file, Integer storageType) throws Exception {
        // 验证是否为图片
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }

        // 上传原图
        FileVO fileVO = upload(file, storageType);

        // 生成缩略图
        try {
            generateThumbnail(file, fileVO, storageType);
        } catch (Exception e) {
            log.error("生成缩略图失败", e);
            // 缩略图生成失败不影响主流程
        }

        return fileVO;
    }

    @Override
    public InputStream download(Long id) throws Exception {
        SysFile file = fileMapper.selectOneById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        // 根据文件的存储类型选择对应的存储策略
        FileStorageStrategy strategy = getFileStorageStrategy(file.getStorageType());
        return strategy.download(file.getFilePath());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) throws Exception {
        SysFile file = fileMapper.selectOneById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        // 删除物理文件
        try {
            // 根据文件的存储类型选择对应的存储策略
            FileStorageStrategy strategy = getFileStorageStrategy(file.getStorageType());
            strategy.delete(file.getFilePath());

            // 删除缩略图
            if (StrUtil.isNotBlank(file.getThumbnailUrl())) {
                String thumbnailPath = file.getFilePath().replace("." + file.getFileExt(), "_thumb." + file.getFileExt());
                strategy.delete(thumbnailPath);
            }
        } catch (Exception e) {
            log.error("删除物理文件失败", e);
        }

        // 删除数据库记录
        fileMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] ids) throws Exception {
        for (Long id : ids) {
            delete(id);
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("文件大小超过限制：" + (maxFileSize / 1024 / 1024) + "MB");
        }

        // 验证文件类型
        String originalName = file.getOriginalFilename();
        if (StrUtil.isBlank(originalName)) {
            throw new BusinessException("文件名不能为空");
        }

        String fileExt = FileUtil.extName(originalName).toLowerCase();
        if (!allowedTypes.contains(fileExt)) {
            throw new BusinessException("不支持的文件类型：" + fileExt);
        }
    }

    /**
     * 生成文件路径（按日期分目录）
     */
    private String generateFilePath(String fileName) {
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return dateDir + "/" + fileName;
    }

    /**
     * 生成缩略图
     */
    private void generateThumbnail(MultipartFile file, FileVO fileVO, Integer storageType) throws Exception {
        // 读取原图
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new BusinessException("无法读取图片");
        }

        // 计算缩略图尺寸（最大200x200）
        int maxSize = 200;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        double scale = Math.min((double) maxSize / width, (double) maxSize / height);
        int thumbnailWidth = (int) (width * scale);
        int thumbnailHeight = (int) (height * scale);

        // 生成缩略图
        BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbnail.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
        g.dispose();

        // 保存缩略图
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String fileExt = FileUtil.extName(fileVO.getFilePath());
        ImageIO.write(thumbnail, fileExt, os);

        String thumbnailPath = fileVO.getFilePath().replace("." + fileExt, "_thumb." + fileExt);
        FileStorageStrategy strategy = getFileStorageStrategy(storageType);
        strategy.upload(
                new ByteArrayInputStream(os.toByteArray()),
                thumbnailPath,
                file.getContentType()
        );

        // 缩略图也使用文件ID作为URL（添加thumb参数区分）
        String thumbnailUrl = "/api/system/file/download/" + fileVO.getId() + "?thumb=true";
        
        // 更新缩略图URL
        SysFile sysFile = new SysFile();
        sysFile.setId(fileVO.getId());
        sysFile.setThumbnailUrl(thumbnailUrl);
        fileMapper.update(sysFile);

        fileVO.setThumbnailUrl(thumbnailUrl);
    }
}
