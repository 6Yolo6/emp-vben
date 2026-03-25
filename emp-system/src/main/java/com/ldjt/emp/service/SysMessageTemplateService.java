package com.ldjt.emp.service;

import com.ldjt.emp.entity.SysMessageTemplate;
import com.mybatisflex.core.paginate.Page;

import java.util.Map;

/**
 * 消息模板服务接口
 */
public interface SysMessageTemplateService {

    /**
     * 创建模板
     */
    void createTemplate(SysMessageTemplate template);

    /**
     * 更新模板
     */
    void updateTemplate(SysMessageTemplate template);

    /**
     * 删除模板
     */
    void deleteTemplate(Long id);

    /**
     * 查询模板详情
     */
    SysMessageTemplate getTemplateById(Long id);

    /**
     * 根据模板编码查询模板
     */
    SysMessageTemplate getTemplateByCode(String templateCode);

    /**
     * 查询模板列表
     */
    Page<SysMessageTemplate> getTemplatePage(String templateName, Integer templateType, Integer pageNum, Integer pageSize);

    /**
     * 获取所有启用的模板列表（用于下拉选择）
     */
    java.util.List<SysMessageTemplate> getTemplateList(Integer status);

    /**
     * 根据模板发送消息
     */
    void sendMessageByTemplate(String templateCode, Map<String, Object> variables, Integer receiverType, java.util.List<Long> receiverIds, java.util.List<Long> receiverRoleIds);
}
