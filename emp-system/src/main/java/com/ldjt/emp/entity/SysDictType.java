package com.ldjt.emp.entity;

import com.ldjt.emp.common.entity.TenantBaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_dict_type")
@Schema(description = "字典类型")
public class SysDictType extends TenantBaseEntity {
    
    @Id(keyType = KeyType.Auto)
    @Schema(description = "字典主键")
    private Long id;
    
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100个字符")
    @Schema(description = "字典名称")
    private String dictName;
    
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Schema(description = "字典类型")
    private String dictType;
    
    @Schema(description = "状态(0停用 1正常)")
    private Integer status;
    
    @Schema(description = "备注")
    private String remark;
}
