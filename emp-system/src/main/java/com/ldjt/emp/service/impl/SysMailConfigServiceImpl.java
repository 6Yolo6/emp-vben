package com.ldjt.emp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.mail.MailConfigDTO;
import com.ldjt.emp.dto.mail.MailSendDTO;
import com.ldjt.emp.entity.SysMailConfig;
import com.ldjt.emp.mapper.SysMailConfigMapper;
import com.ldjt.emp.service.SysMailConfigService;
import com.ldjt.emp.service.SysMailService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.ldjt.emp.entity.table.SysMailConfigTableDef.SYS_MAIL_CONFIG;

/**
 * 邮件配置服务实现类
 *
 * @author kiro
 */
@Service
@RequiredArgsConstructor
public class SysMailConfigServiceImpl extends ServiceImpl<SysMailConfigMapper, SysMailConfig> implements SysMailConfigService {

    private final SysMailService mailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createConfig(MailConfigDTO mailConfigDTO) {
        // 检查配置名称是否重复
        long count = count(QueryWrapper.create().where(SYS_MAIL_CONFIG.CONFIG_NAME.eq(mailConfigDTO.getConfigName())));
        if (count > 0) {
            throw new BusinessException("配置名称已存在");
        }

        // 如果设置为默认配置，取消其他默认配置
        if (mailConfigDTO.getIsDefault() != null && mailConfigDTO.getIsDefault() == 1) {
            // 将所有配置的isDefault设置为0
            SysMailConfig updateEntity = new SysMailConfig();
            updateEntity.setIsDefault(0);
            update(updateEntity, QueryWrapper.create());
        }

        // 创建配置
        SysMailConfig config = BeanUtil.copyProperties(mailConfigDTO, SysMailConfig.class);
        return save(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConfig(MailConfigDTO mailConfigDTO) {
        // 检查配置是否存在
        SysMailConfig existConfig = getById(mailConfigDTO.getId());
        if (existConfig == null) {
            throw new BusinessException("邮件配置不存在");
        }

        // 检查配置名称是否重复
        long count = count(QueryWrapper.create()
                .where(SYS_MAIL_CONFIG.CONFIG_NAME.eq(mailConfigDTO.getConfigName()))
                .and(SYS_MAIL_CONFIG.ID.ne(mailConfigDTO.getId())));
        if (count > 0) {
            throw new BusinessException("配置名称已存在");
        }

        // 如果设置为默认配置，取消其他默认配置
        if (mailConfigDTO.getIsDefault() != null && mailConfigDTO.getIsDefault() == 1) {
            // 将其他配置的isDefault设置为0
            SysMailConfig updateEntity = new SysMailConfig();
            updateEntity.setIsDefault(0);
            update(updateEntity, QueryWrapper.create().where(SYS_MAIL_CONFIG.ID.ne(mailConfigDTO.getId())));
        }

        // 更新配置
        SysMailConfig config = BeanUtil.copyProperties(mailConfigDTO, SysMailConfig.class);
        return updateById(config);
    }

    @Override
    public Boolean deleteConfig(Long id) {
        // 检查配置是否存在
        SysMailConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("邮件配置不存在");
        }

        // 不允许删除默认配置
        if (config.getIsDefault() != null && config.getIsDefault() == 1) {
            throw new BusinessException("不允许删除默认配置");
        }

        return removeById(id);
    }

    @Override
    public Boolean testConfig(Long id, String toEmail) {
        // 检查配置是否存在
        SysMailConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("邮件配置不存在");
        }

        // 发送测试邮件
        MailSendDTO mailSendDTO = new MailSendDTO();
        mailSendDTO.setToEmails(Collections.singletonList(toEmail));
        mailSendDTO.setSubject("邮件配置测试");
        mailSendDTO.setContent("这是一封测试邮件，如果您收到此邮件，说明邮件配置正确。");
        mailSendDTO.setIsHtml(false);
        mailSendDTO.setConfigId(id);

        return mailService.sendMail(mailSendDTO);
    }

    @Override
    public Page<SysMailConfig> pageConfig(Page<SysMailConfig> page, String configName) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SYS_MAIL_CONFIG.CONFIG_NAME.like(configName, StrUtil.isNotBlank(configName)))
                .orderBy(SYS_MAIL_CONFIG.IS_DEFAULT.desc())
                .orderBy(SYS_MAIL_CONFIG.CREATE_TIME.desc());

        return page(page, wrapper);
    }
}
