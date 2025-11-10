package com.ldjt.emp.service;

import com.mybatisflex.core.paginate.Page;
import com.ldjt.emp.dto.config.ConfigCreateDTO;
import com.ldjt.emp.dto.config.ConfigQueryDTO;
import com.ldjt.emp.dto.config.ConfigUpdateDTO;
import com.ldjt.emp.vo.config.ConfigVO;

/**
 * 系统参数配置服务接口
 *
 * @author system
 */
public interface SysConfigService {

    /**
     * 分页查询系统参数
     */
    Page<ConfigVO> page(ConfigQueryDTO queryDTO);

    /**
     * 根据ID查询系统参数
     */
    ConfigVO getById(Long id);

    /**
     * 根据参数键名查询参数值
     */
    String getConfigValueByKey(String configKey);

    /**
     * 创建系统参数
     */
    Long create(ConfigCreateDTO createDTO);

    /**
     * 更新系统参数
     */
    void update(ConfigUpdateDTO updateDTO);

    /**
     * 删除系统参数
     */
    void delete(Long id);

    /**
     * 批量删除系统参数
     */
    void deleteBatch(Long[] ids);

    /**
     * 刷新参数缓存
     */
    void refreshCache();

    /**
     * 清空参数缓存
     */
    void clearCache();
}
