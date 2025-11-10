package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统文件实体
 *
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_file")
@Schema(description = "系统文件")
public class SysFile extends TenantBaseEntity {

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

    @Schema(description = "存储桶名称")
    private String bucketName;

    @Schema(description = "对象键（MinIO/OSS）")
    private String objectKey;

    @Schema(description = "缩略图URL（图片文件）")
    private String thumbnailUrl;

    @Schema(description = "文件MD5值")
    private String md5;

    @Schema(description = "状态：0=失败 1=成功")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
