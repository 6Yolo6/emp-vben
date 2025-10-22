package com.ldjt.emp.dto;

import lombok.Data;

/**
 * 用户分页查询DTO
 * 
 * @author emp
 */
@Data
public class UserPageQueryDTO {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickName;
    
    /**
     * 手机号
     */
    private String phoneNumber;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 部门ID
     */
    private Long deptId;
    
    /**
     * 开始时间
     */
    private String beginTime;
    
    /**
     * 结束时间
     */
    private String endTime;
}
