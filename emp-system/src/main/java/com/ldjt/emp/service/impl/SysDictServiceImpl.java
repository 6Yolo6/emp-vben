package com.ldjt.emp.service.impl;

import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.entity.SysDictData;
import com.ldjt.emp.entity.SysDictType;
import com.ldjt.emp.mapper.SysDictDataMapper;
import com.ldjt.emp.mapper.SysDictTypeMapper;
import com.ldjt.emp.service.SysDictService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ldjt.emp.entity.table.SysDictDataTableDef.SYS_DICT_DATA;
import static com.ldjt.emp.entity.table.SysDictTypeTableDef.SYS_DICT_TYPE;

/**
 * 字典服务实现
 */
@Slf4j
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictService {
    
    private static final String DICT_CACHE_KEY = "sys:dict:";
    private static final long CACHE_EXPIRE_HOURS = 24;
    
    @Autowired
    private SysDictTypeMapper dictTypeMapper;
    
    @Autowired
    private SysDictDataMapper dictDataMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // ========== 字典类型管理 ==========
    
    @Override
    public Page<SysDictType> pageDictTypes(int pageNum, int pageSize, String dictName, String dictType, Integer status) {
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_DICT_TYPE.DELETED.eq(0))
                .and(SYS_DICT_TYPE.DICT_NAME.like(dictName, dictName != null && !dictName.trim().isEmpty()))
                .and(SYS_DICT_TYPE.DICT_TYPE.like(dictType, dictType != null && !dictType.trim().isEmpty()))
                .and(SYS_DICT_TYPE.STATUS.eq(status, status != null))
                .orderBy(SYS_DICT_TYPE.CREATE_TIME.desc());
        
        return dictTypeMapper.paginate(new Page<>(pageNum, pageSize), query);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictType(SysDictType dictType) {
        // 检查字典类型是否已存在
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_DICT_TYPE.DICT_TYPE.eq(dictType.getDictType()))
                .and(SYS_DICT_TYPE.DELETED.eq(0));
        
        if (dictTypeMapper.selectCountByQuery(query) > 0) {
            throw new BusinessException("字典类型已存在");
        }
        
        return dictTypeMapper.insert(dictType) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(SysDictType dictType) {
        // 检查字典类型是否存在
        SysDictType existing = dictTypeMapper.selectOneById(dictType.getId());
        if (existing == null) {
            throw new BusinessException("字典类型不存在");
        }
        
        // 如果修改了字典类型，检查新类型是否已存在
        if (!existing.getDictType().equals(dictType.getDictType())) {
            QueryWrapper query = QueryWrapper.create()
                    .where(SYS_DICT_TYPE.DICT_TYPE.eq(dictType.getDictType()))
                    .and(SYS_DICT_TYPE.ID.ne(dictType.getId()))
                    .and(SYS_DICT_TYPE.DELETED.eq(0));
            
            if (dictTypeMapper.selectCountByQuery(query) > 0) {
                throw new BusinessException("字典类型已存在");
            }
            
            // 更新字典数据中的类型
            QueryWrapper updateQuery = QueryWrapper.create()
                    .where(SYS_DICT_DATA.DICT_TYPE.eq(existing.getDictType()));
            
            SysDictData updateData = new SysDictData();
            updateData.setDictType(dictType.getDictType());
            
            dictDataMapper.updateByQuery(updateData, updateQuery);
        }
        
        boolean result = dictTypeMapper.update(dictType) > 0;
        
        // 清除缓存
        if (result) {
            clearDictCache(existing.getDictType());
            if (!existing.getDictType().equals(dictType.getDictType())) {
                clearDictCache(dictType.getDictType());
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictType(Long id) {
        SysDictType dictType = dictTypeMapper.selectOneById(id);
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }
        
        // 检查是否有字典数据
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_DICT_DATA.DICT_TYPE.eq(dictType.getDictType()))
                .and(SYS_DICT_DATA.DELETED.eq(0));
        
        if (dictDataMapper.selectCountByQuery(query) > 0) {
            throw new BusinessException("该字典类型下存在字典数据，无法删除");
        }
        
        boolean result = dictTypeMapper.deleteById(id) > 0;
        
        // 清除缓存
        if (result) {
            clearDictCache(dictType.getDictType());
        }
        
        return result;
    }
    
    @Override
    public void refreshCache() {
        // 清除所有字典缓存
        redisTemplate.delete(redisTemplate.keys(DICT_CACHE_KEY + "*"));
        log.info("字典缓存已刷新");
    }
    
    // ========== 字典数据管理 ==========
    
    @Override
    public List<SysDictData> getDictDataByType(String dictType) {
        // 先从缓存获取
        String cacheKey = DICT_CACHE_KEY + dictType;
        @SuppressWarnings("unchecked")
        List<SysDictData> cachedData = (List<SysDictData>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedData != null) {
            return cachedData;
        }
        
        // 从数据库查询
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_DICT_DATA.DICT_TYPE.eq(dictType))
                .and(SYS_DICT_DATA.STATUS.eq(1))
                .and(SYS_DICT_DATA.DELETED.eq(0))
                .orderBy(SYS_DICT_DATA.DICT_SORT.asc());
        
        List<SysDictData> dataList = dictDataMapper.selectListByQuery(query);
        
        // 存入缓存
        redisTemplate.opsForValue().set(cacheKey, dataList, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        return dataList;
    }
    
    @Override
    public Page<SysDictData> pageDictData(int pageNum, int pageSize, String dictType, String dictLabel, Integer status) {
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_DICT_DATA.DELETED.eq(0))
                .and(SYS_DICT_DATA.DICT_TYPE.eq(dictType, dictType != null && !dictType.trim().isEmpty()))
                .and(SYS_DICT_DATA.DICT_LABEL.like(dictLabel, dictLabel != null && !dictLabel.trim().isEmpty()))
                .and(SYS_DICT_DATA.STATUS.eq(status, status != null))
                .orderBy(SYS_DICT_DATA.DICT_SORT.asc());
        
        return dictDataMapper.paginate(new Page<>(pageNum, pageSize), query);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictData(SysDictData dictData) {
        boolean result = dictDataMapper.insert(dictData) > 0;
        
        // 清除缓存
        if (result) {
            clearDictCache(dictData.getDictType());
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictData(SysDictData dictData) {
        SysDictData existing = dictDataMapper.selectOneById(dictData.getId());
        if (existing == null) {
            throw new BusinessException("字典数据不存在");
        }
        
        boolean result = dictDataMapper.update(dictData) > 0;
        
        // 清除缓存
        if (result) {
            clearDictCache(existing.getDictType());
            if (!existing.getDictType().equals(dictData.getDictType())) {
                clearDictCache(dictData.getDictType());
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictData(Long id) {
        SysDictData dictData = dictDataMapper.selectOneById(id);
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }
        
        boolean result = dictDataMapper.deleteById(id) > 0;
        
        // 清除缓存
        if (result) {
            clearDictCache(dictData.getDictType());
        }
        
        return result;
    }
    
    @Override
    public String getDictLabel(String dictType, String dictValue) {
        List<SysDictData> dataList = getDictDataByType(dictType);
        return dataList.stream()
                .filter(data -> data.getDictValue().equals(dictValue))
                .map(SysDictData::getDictLabel)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 清除字典缓存
     */
    private void clearDictCache(String dictType) {
        String cacheKey = DICT_CACHE_KEY + dictType;
        redisTemplate.delete(cacheKey);
        log.info("已清除字典缓存: {}", dictType);
    }
}
