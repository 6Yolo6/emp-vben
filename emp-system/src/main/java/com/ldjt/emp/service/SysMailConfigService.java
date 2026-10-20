package com.ldjt.emp.service;

import com.ldjt.emp.dto.mail.MailConfigDTO;
import com.ldjt.emp.entity.SysMailConfig;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 邮件配置服务接口
 *
 * @author kiro
 */
public interface SysMailConfigService extends IService<SysMailConfig> {

    /**
     * 创建邮件配置
     *
     * @param mailConfigDTO 邮件配置DTO
     * @return 是否创建成功
     */
    Boolean createConfig(MailConfigDTO mailConfigDTO);

    /**
     * 更新邮件配置
     *
     * @param mailConfigDTO 邮件配置DTO
     * @return 是否更新成功
     */
    Boolean updateConfig(MailConfigDTO mailConfigDTO);

    /**
     * 删除邮件配置
     *
     * @param id 配置ID
     * @return 是否删除成功
     */
    Boolean deleteConfig(Long id);

    /**
     * 测试邮件配置
     *
     * @param id 配置ID
     * @param toEmail 测试收件人
     * @return 是否测试成功
     */
    Boolean testConfig(Long id, String toEmail);

    /**
     * 分页查询邮件配置
     *
     * @param page 分页参数
     * @param configName 配置名称
     * @return 分页结果
     */
    Page<SysMailConfig> pageConfig(Page<SysMailConfig> page, String configName);
}
