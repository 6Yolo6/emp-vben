package com.ldjt.emp.service;

import com.ldjt.emp.entity.SysDept;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 部门服务接口
 * 
 * @author emp
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 查询部门树
     * 
     * @return 部门树列表
     */
    List<SysDept> getDeptTree();
}
