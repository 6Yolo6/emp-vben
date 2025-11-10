package com.ldjt.emp.vo.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件VO
 *
 * @author system
 */
@Data
@Schema(description = "文件VO")
public class FileVO {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型（MIME类型）")
    private String fileType;

    @Schema(description = "文件扩展名")
    private String fileExt;

    @Schema(description = "存储类型：0=本地存储 1=MinIO 2=阿里云OSS")
    private Integer storageType;

    @Schema(description = "缩略图URL（图片文件）")
    private String thumbnailUrl;

    @Schema(description = "文件MD5值")
    private String md5;

    @Schema(description = "状态：0=失败 1=成功")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private Long createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
