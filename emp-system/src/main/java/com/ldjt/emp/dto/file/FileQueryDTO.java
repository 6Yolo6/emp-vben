package com.ldjt.emp.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件查询DTO
 *
 * @author system
 */
@Data
@Schema(description = "文件查询DTO")
public class FileQueryDTO {

    @Schema(description = "文件名称（模糊查询）")
    private String fileName;

    @Schema(description = "原始文件名（模糊查询）")
    private String originalName;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "存储类型：0=本地存储 1=MinIO 2=阿里云OSS")
    private Integer storageType;

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页数量")
    private Integer pageSize = 10;
}
