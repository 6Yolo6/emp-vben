package com.ldjt.emp.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门树DTO
 * 
 * @author emp
 */
@Data
public class DeptTreeDTO {
    
    private Long id;
    
    private Long parentId;
    
    private String deptName;
    
    private Integer orderNum;
    
    private String leader;
    
    private String phone;
    
    private String email;
    
    private Integer status;
    
    private List<DeptTreeDTO> children = new ArrayList<>();
}
