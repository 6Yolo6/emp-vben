package com.ldjt.emp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.dto.file.FileQueryDTO;
import com.ldjt.emp.service.SysFileService;
import com.ldjt.emp.vo.file.FileVO;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 系统文件控制器
 *
 * @author system
 */
@Tag(name = "系统文件管理")
@RestController
@RequestMapping("/system/file")
@RequiredArgsConstructor
public class SysFileController {

    private final SysFileService fileService;

    @Operation(summary = "分页查询文件")
    @GetMapping("/page")
    @SaCheckPermission("system:file:query")
    public Result<Page<FileVO>> page(@Valid FileQueryDTO queryDTO) {
        return Result.success(fileService.page(queryDTO));
    }

    @Operation(summary = "根据ID查询文件")
    @GetMapping("/{id}")
    @SaCheckPermission("system:file:query")
    public Result<FileVO> getById(@Parameter(description = "文件ID") @PathVariable Long id) {
        return Result.success(fileService.getById(id));
    }

    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("system:file:upload")
    public Result<FileVO> upload(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储类型：0=本地 1=MinIO") @RequestParam(value = "storageType", required = false) Integer storageType
    ) {
        try {
            return Result.success(fileService.upload(file, storageType));
        } catch (Exception e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "上传图片（生成缩略图）")
    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("system:file:upload")
    public Result<FileVO> uploadImage(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储类型：0=本地 1=MinIO") @RequestParam(value = "storageType", required = false) Integer storageType
    ) {
        try {
            return Result.success(fileService.uploadImage(file, storageType));
        } catch (Exception e) {
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "下载文件")
    @GetMapping("/download/{id}")
    public void download(
            @Parameter(description = "文件ID") @PathVariable Long id,
            @Parameter(description = "是否下载缩略图") @RequestParam(value = "thumb", required = false, defaultValue = "false") Boolean thumb,
            @Parameter(description = "是否强制下载") @RequestParam(value = "download", required = false, defaultValue = "false") Boolean forceDownload,
            HttpServletResponse response
    ) {
        try {
            FileVO fileVO = fileService.getById(id);
            
            // 获取文件路径
            String filePath = fileVO.getFilePath();
            if (thumb != null && thumb) {
                // 下载缩略图
                String fileExt = fileVO.getFileExt();
                filePath = filePath.replace("." + fileExt, "_thumb." + fileExt);
            }
            
            InputStream inputStream = fileService.download(id);

            // 设置响应头
            response.setContentType(fileVO.getFileType());
            
            // 根据参数决定是预览还是下载
            String disposition;
            if (forceDownload != null && forceDownload) {
                disposition = "attachment";
            } else {
                // 图片预览，其他文件下载
                disposition = fileVO.getFileType().startsWith("image/") ? "inline" : "attachment";
            }
            
            response.setHeader("Content-Disposition",
                    disposition + "; filename=" + URLEncoder.encode(fileVO.getOriginalName(), StandardCharsets.UTF_8));

            // 写入响应流
            StreamUtils.copy(inputStream, response.getOutputStream());
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:file:remove")
    public Result<Void> delete(@Parameter(description = "文件ID") @PathVariable Long id) {
        try {
            fileService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("文件删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量删除文件")
    @DeleteMapping("/batch")
    @SaCheckPermission("system:file:remove")
    public Result<Void> deleteBatch(@Parameter(description = "文件ID数组") @RequestBody Long[] ids) {
        try {
            fileService.deleteBatch(ids);
            return Result.success();
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }
}
