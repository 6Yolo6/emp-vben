package com.ldjt.emp.service.impl;

import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.message.MessageSendDTO;
import com.ldjt.emp.entity.SysMessageTemplate;
import com.ldjt.emp.framework.tenant.TenantContextHolder;
import com.ldjt.emp.mapper.SysMessageTemplateMapper;
import com.ldjt.emp.service.SysMessageService;
import com.ldjt.emp.service.SysMessageTemplateService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ldjt.emp.entity.table.SysMessageTemplateTableDef.SYS_MESSAGE_TEMPLATE;

/**
 * 消息模板服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMessageTemplateServiceImpl implements SysMessageTemplateService {

    private final SysMessageTemplateMapper templateMapper;
    private final SysMessageService messageService;

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTemplate(SysMessageTemplate template) {
        Long tenantId = TenantContextHolder.getTenantId();
        template.setTenantId(tenantId);
        template.setVersion(1);

        // 检查模板编码是否已存在
        SysMessageTemplate existing = templateMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SYS_MESSAGE_TEMPLATE.TEMPLATE_CODE.eq(template.getTemplateCode()))
                        .and(SYS_MESSAGE_TEMPLATE.TENANT_ID.eq(tenantId))
                        .and(SYS_MESSAGE_TEMPLATE.DELETED.eq(0))
        );

        if (existing != null) {
            throw new BusinessException("模板编码已存在");
        }

        templateMapper.insert(template);
        log.info("创建消息模板成功，模板编码：{}", template.getTemplateCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(SysMessageTemplate template) {
        Long tenantId = TenantContextHolder.getTenantId();

        SysMessageTemplate existing = templateMapper.selectOneById(template.getId());
        if (existing == null || !existing.getTenantId().equals(tenantId)) {
            throw new BusinessException("模板不存在");
        }

        // 检查模板编码是否与其他模板重复
        SysMessageTemplate duplicate = templateMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SYS_MESSAGE_TEMPLATE.TEMPLATE_CODE.eq(template.getTemplateCode()))
                        .and(SYS_MESSAGE_TEMPLATE.TENANT_ID.eq(tenantId))
                        .and(SYS_MESSAGE_TEMPLATE.ID.ne(template.getId()))
                        .and(SYS_MESSAGE_TEMPLATE.DELETED.eq(0))
        );

        if (duplicate != null) {
            throw new BusinessException("模板编码已存在");
        }

        // 更新版本号
        template.setVersion(existing.getVersion() + 1);
        templateMapper.update(template);
        log.info("更新消息模板成功，模板ID：{}", template.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id) {
        Long tenantId = TenantContextHolder.getTenantId();

        SysMessageTemplate template = templateMapper.selectOneById(id);
        if (template == null || !template.getTenantId().equals(tenantId)) {
            throw new BusinessException("模板不存在");
        }

        // 检查模板是否被使用（查询是否有消息使用了该模板）
        // 这里可以根据业务需求决定是否允许删除正在使用的模板
        // 暂时允许删除，实际项目中可以添加使用记录表来跟踪模板使用情况

        template.setDeleted(1);
        templateMapper.update(template);
        log.info("删除消息模板成功，模板ID：{}", id);
    }

    @Override
    public SysMessageTemplate getTemplateById(Long id) {
        Long tenantId = TenantContextHolder.getTenantId();

        SysMessageTemplate template = templateMapper.selectOneById(id);
        if (template == null || !template.getTenantId().equals(tenantId)) {
            throw new BusinessException("模板不存在");
        }

        return template;
    }

    @Override
    public SysMessageTemplate getTemplateByCode(String templateCode) {
        Long tenantId = TenantContextHolder.getTenantId();

        return templateMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SYS_MESSAGE_TEMPLATE.TEMPLATE_CODE.eq(templateCode))
                        .and(SYS_MESSAGE_TEMPLATE.TENANT_ID.eq(tenantId))
                        .and(SYS_MESSAGE_TEMPLATE.STATUS.eq(1))
                        .and(SYS_MESSAGE_TEMPLATE.DELETED.eq(0))
        );
    }

    @Override
    public Page<SysMessageTemplate> getTemplatePage(String templateName, Integer templateType, Integer pageNum, Integer pageSize) {
        Long tenantId = TenantContextHolder.getTenantId();

        QueryWrapper query = QueryWrapper.create()
                .where(SYS_MESSAGE_TEMPLATE.TENANT_ID.eq(tenantId))
                .and(SYS_MESSAGE_TEMPLATE.DELETED.eq(0));

        if (templateName != null && !templateName.isEmpty()) {
            query.and(SYS_MESSAGE_TEMPLATE.TEMPLATE_NAME.like(templateName));
        }

        if (templateType != null) {
            query.and(SYS_MESSAGE_TEMPLATE.TEMPLATE_TYPE.eq(templateType));
        }

        query.orderBy(SYS_MESSAGE_TEMPLATE.CREATE_TIME.desc());

        return templateMapper.paginate(Page.of(pageNum, pageSize), query);
    }

    @Override
    public List<SysMessageTemplate> getTemplateList(Integer status) {
        Long tenantId = TenantContextHolder.getTenantId();

        QueryWrapper query = QueryWrapper.create()
                .where(SYS_MESSAGE_TEMPLATE.TENANT_ID.eq(tenantId))
                .and(SYS_MESSAGE_TEMPLATE.DELETED.eq(0));

        if (status != null) {
            query.and(SYS_MESSAGE_TEMPLATE.STATUS.eq(status));
        }

        query.orderBy(SYS_MESSAGE_TEMPLATE.CREATE_TIME.desc());

        return templateMapper.selectListByQuery(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessageByTemplate(String templateCode, Map<String, Object> variables, 
                                     Integer receiverType, List<Long> receiverIds, List<Long> receiverRoleIds) {
        // 查询模板
        SysMessageTemplate template = getTemplateByCode(templateCode);
        if (template == null) {
            throw new BusinessException("模板不存在或已禁用");
        }

        // 替换变量
        String title = replaceVariables(template.getTitleTemplate(), variables);
        String content = replaceVariables(template.getContentTemplate(), variables);

        // 发送消息
        MessageSendDTO dto = new MessageSendDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setMessageType(template.getTemplateType());
        dto.setReceiverType(receiverType);
        dto.setReceiverIds(receiverIds);
        dto.setReceiverRoleIds(receiverRoleIds);

        messageService.sendMessage(dto);
        log.info("根据模板发送消息成功，模板编码：{}", templateCode);
    }

    /**
     * 替换模板变量
     */
    private String replaceVariables(String template, Map<String, Object> variables) {
        if (template == null || variables == null) {
            return template;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.get(variableName);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
