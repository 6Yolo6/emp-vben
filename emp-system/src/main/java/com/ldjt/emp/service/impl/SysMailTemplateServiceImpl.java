package com.ldjt.emp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.mail.MailTemplateDTO;
import com.ldjt.emp.entity.SysMailTemplate;
import com.ldjt.emp.mapper.SysMailTemplateMapper;
import com.ldjt.emp.service.SysMailTemplateService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ldjt.emp.entity.table.SysMailTemplateTableDef.SYS_MAIL_TEMPLATE;

/**
 * 邮件模板服务实现类
 *
 * @author kiro
 */
@Service
public class SysMailTemplateServiceImpl extends ServiceImpl<SysMailTemplateMapper, SysMailTemplate>
        implements SysMailTemplateService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createTemplate(MailTemplateDTO mailTemplateDTO) {
        // 检查模板编码是否重复
        long count = count(QueryWrapper.create()
                .where(SYS_MAIL_TEMPLATE.TEMPLATE_CODE.eq(mailTemplateDTO.getTemplateCode())));
        if (count > 0) {
            throw new BusinessException("模板编码已存在");
        }

        // 创建模板
        SysMailTemplate template = BeanUtil.copyProperties(mailTemplateDTO, SysMailTemplate.class);
        return save(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTemplate(MailTemplateDTO mailTemplateDTO) {
        // 检查模板是否存在
        SysMailTemplate existTemplate = getById(mailTemplateDTO.getId());
        if (existTemplate == null) {
            throw new BusinessException("邮件模板不存在");
        }

        // 检查模板编码是否重复
        long count = count(QueryWrapper.create()
                .where(SYS_MAIL_TEMPLATE.TEMPLATE_CODE.eq(mailTemplateDTO.getTemplateCode()))
                .and(SYS_MAIL_TEMPLATE.ID.ne(mailTemplateDTO.getId())));
        if (count > 0) {
            throw new BusinessException("模板编码已存在");
        }

        // 更新模板
        SysMailTemplate template = BeanUtil.copyProperties(mailTemplateDTO, SysMailTemplate.class);
        return updateById(template);
    }

    @Override
    public Boolean deleteTemplate(Long id) {
        // 检查模板是否存在
        SysMailTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("邮件模板不存在");
        }

        return removeById(id);
    }

    @Override
    public Page<SysMailTemplate> pageTemplate(Page<SysMailTemplate> page, String templateName) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SYS_MAIL_TEMPLATE.TEMPLATE_NAME.like(templateName, StrUtil.isNotBlank(templateName)))
                .orderBy(SYS_MAIL_TEMPLATE.CREATE_TIME.desc());

        return page(page, wrapper);
    }
}
