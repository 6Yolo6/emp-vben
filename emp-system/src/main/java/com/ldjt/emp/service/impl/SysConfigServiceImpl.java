package com.ldjt.emp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.config.ConfigCreateDTO;
import com.ldjt.emp.dto.config.ConfigQueryDTO;
import com.ldjt.emp.dto.config.ConfigUpdateDTO;
import com.ldjt.emp.entity.SysConfig;
import com.ldjt.emp.mapper.SysConfigMapper;
import com.ldjt.emp.service.SysConfigService;
import com.ldjt.emp.vo.config.ConfigVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ldjt.emp.entity.table.SysConfigTableDef.SYS_CONFIG;

/**
 * 系统参数配置服务实现
 *
 * @author system
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper configMapper;

    private static final String CACHE_NAME = "sys_config";
    private static final String CACHE_KEY_PREFIX = "config:";

    @Override
    public Page<ConfigVO> page(ConfigQueryDTO queryDTO) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(SYS_CONFIG)
                .where(SYS_CONFIG.CONFIG_NAME.like(queryDTO.getConfigName(), StrUtil::isNotBlank))
                .and(SYS_CONFIG.CONFIG_KEY.like(queryDTO.getConfigKey(), StrUtil::isNotBlank))
                .and(SYS_CONFIG.CONFIG_TYPE.eq(queryDTO.getConfigType(), queryDTO.getConfigType() != null))
                .orderBy(SYS_CONFIG.CREATE_TIME.desc());

        Page<SysConfig> page = configMapper.paginate(
                new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()),
                queryWrapper
        );

        List<ConfigVO> voList = BeanUtil.copyToList(page.getRecords(), ConfigVO.class);
        
        Page<ConfigVO> voPage = new Page<>();
        voPage.setPageNumber(page.getPageNumber());
        voPage.setPageSize(page.getPageSize());
        voPage.setTotalRow(page.getTotalRow());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public ConfigVO getById(Long id) {
        SysConfig config = configMapper.selectOneById(id);
        if (config == null) {
            throw new BusinessException("参数配置不存在");
        }
        return BeanUtil.copyProperties(config, ConfigVO.class);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'" + CACHE_KEY_PREFIX + "' + #configKey", unless = "#result == null")
    public String getConfigValueByKey(String configKey) {
        if (StrUtil.isBlank(configKey)) {
            return null;
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(SYS_CONFIG.CONFIG_VALUE)
                .from(SYS_CONFIG)
                .where(SYS_CONFIG.CONFIG_KEY.eq(configKey))
                .limit(1);

        SysConfig config = configMapper.selectOneByQuery(queryWrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CACHE_NAME, key = "'" + CACHE_KEY_PREFIX + "' + #createDTO.configKey")
    public Long create(ConfigCreateDTO createDTO) {
        // 检查参数键名是否已存在
        checkConfigKeyUnique(null, createDTO.getConfigKey());

        SysConfig config = BeanUtil.copyProperties(createDTO, SysConfig.class);
        configMapper.insert(config);
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CACHE_NAME, key = "'" + CACHE_KEY_PREFIX + "' + #updateDTO.configKey")
    public void update(ConfigUpdateDTO updateDTO) {
        SysConfig existConfig = configMapper.selectOneById(updateDTO.getId());
        if (existConfig == null) {
            throw new BusinessException("参数配置不存在");
        }

        // 检查参数键名是否已存在
        checkConfigKeyUnique(updateDTO.getId(), updateDTO.getConfigKey());

        // 系统内置参数不允许修改类型
        if (existConfig.getConfigType() == 0 && !existConfig.getConfigType().equals(updateDTO.getConfigType())) {
            throw new BusinessException("系统内置参数不允许修改类型");
        }

        SysConfig config = BeanUtil.copyProperties(updateDTO, SysConfig.class);
        configMapper.update(config);

        // 如果键名发生变化，需要清除旧键名的缓存
        if (!existConfig.getConfigKey().equals(updateDTO.getConfigKey())) {
            clearCacheByKey(existConfig.getConfigKey());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysConfig config = configMapper.selectOneById(id);
        if (config == null) {
            throw new BusinessException("参数配置不存在");
        }

        // 系统内置参数不允许删除
        if (config.getConfigType() == 0) {
            throw new BusinessException("系统内置参数不允许删除");
        }

        configMapper.deleteById(id);
        clearCacheByKey(config.getConfigKey());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void refreshCache() {
        log.info("刷新系统参数缓存");
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void clearCache() {
        log.info("清空系统参数缓存");
    }

    /**
     * 检查参数键名唯一性
     */
    private void checkConfigKeyUnique(Long id, String configKey) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(SYS_CONFIG.ID)
                .from(SYS_CONFIG)
                .where(SYS_CONFIG.CONFIG_KEY.eq(configKey))
                .and(SYS_CONFIG.ID.ne(id, id != null))
                .limit(1);

        SysConfig existConfig = configMapper.selectOneByQuery(queryWrapper);
        if (existConfig != null) {
            throw new BusinessException("参数键名已存在：" + configKey);
        }
    }

    /**
     * 清除指定键名的缓存
     */
    @CacheEvict(value = CACHE_NAME, key = "'" + CACHE_KEY_PREFIX + "' + #configKey")
    public void clearCacheByKey(String configKey) {
        log.info("清除参数缓存：{}", configKey);
    }
}
