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
 * 字典数据实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_dict_data")
@Schema(description = "字典数据")
public class SysDictData extends TenantBaseEntity {
    
    @Id(keyType = KeyType.Auto)
    @Schema(description = "字典编码")
    private Long id;
    
    @Schema(description = "字典排序")
    private Integer dictSort;
    
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    @Schema(description = "字典标签")
    private String dictLabel;
    
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    @Schema(description = "字典键值")
    private String dictValue;
    
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Schema(description = "字典类型")
    private String dictType;
    
    @Schema(description = "样式属性")
    private String cssClass;
    
    @Schema(description = "表格回显样式")
    private String listClass;
    
    @Schema(description = "是否默认(0否 1是)")
    private Integer isDefault;
    
    @Schema(description = "状态(0停用 1正常)")
    private Integer status;
    
    @Schema(description = "备注")
    private String remark;
}
