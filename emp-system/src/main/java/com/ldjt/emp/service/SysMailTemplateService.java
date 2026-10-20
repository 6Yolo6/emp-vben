package com.ldjt.emp.service;

import com.ldjt.emp.dto.mail.MailTemplateDTO;
import com.ldjt.emp.entity.SysMailTemplate;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 邮件模板服务接口
 *
 * @author kiro
 */
public interface SysMailTemplateService extends IService<SysMailTemplate> {

    /**
     * 创建邮件模板
     */
    Boolean createTemplate(MailTemplateDTO mailTemplateDTO);

    /**
     * 更新邮件模板
     */
    Boolean updateTemplate(MailTemplateDTO mailTemplateDTO);

    /**
     * 删除邮件模板
     */
    Boolean deleteTemplate(Long id);

    /**
     * 分页查询邮件模板
     */
    Page<SysMailTemplate> pageTemplate(Page<SysMailTemplate> page, String templateName);
}
