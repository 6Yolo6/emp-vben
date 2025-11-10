package com.ldjt.emp.service;

import com.ldjt.emp.entity.SysDictData;
import com.ldjt.emp.entity.SysDictType;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 字典服务接口
 */
public interface SysDictService extends IService<SysDictType> {
    
    // ========== 字典类型管理 ==========
    
    /**
     * 分页查询字典类型
     */
    Page<SysDictType> pageDictTypes(int pageNum, int pageSize, String dictName, String dictType, Integer status);
    
    /**
     * 创建字典类型
     */
    boolean createDictType(SysDictType dictType);
    
    /**
     * 更新字典类型
     */
    boolean updateDictType(SysDictType dictType);
    
    /**
     * 删除字典类型
     */
    boolean deleteDictType(Long id);
    
    /**
     * 刷新字典缓存
     */
    void refreshCache();
    
    // ========== 字典数据管理 ==========
    
    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> getDictDataByType(String dictType);
    
    /**
     * 分页查询字典数据
     */
    Page<SysDictData> pageDictData(int pageNum, int pageSize, String dictType, String dictLabel, Integer status);
    
    /**
     * 创建字典数据
     */
    boolean createDictData(SysDictData dictData);
    
    /**
     * 更新字典数据
     */
    boolean updateDictData(SysDictData dictData);
    
    /**
     * 删除字典数据
     */
    boolean deleteDictData(Long id);
    
    /**
     * 根据字典类型和字典值获取字典标签
     */
    String getDictLabel(String dictType, String dictValue);
}
