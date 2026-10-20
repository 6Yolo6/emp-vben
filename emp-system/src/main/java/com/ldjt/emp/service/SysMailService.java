package com.ldjt.emp.service;

import com.ldjt.emp.dto.mail.MailSendDTO;
import com.ldjt.emp.dto.mail.MailTemplateDTO;

/**
 * 邮件服务接口
 *
 * @author kiro
 */
public interface SysMailService {

    /**
     * 发送邮件
     *
     * @param mailSendDTO 邮件发送DTO
     * @return 是否发送成功
     */
    Boolean sendMail(MailSendDTO mailSendDTO);

    /**
     * 使用模板发送邮件
     *
     * @param mailTemplateDTO 模板邮件DTO
     * @return 是否发送成功
     */
    Boolean sendTemplateMail(MailTemplateDTO mailTemplateDTO);

    /**
     * 异步发送邮件
     *
     * @param mailSendDTO 邮件发送DTO
     */
    void sendMailAsync(MailSendDTO mailSendDTO);

    /**
     * 重试发送失败的邮件
     *
     * @param logId 邮件日志ID
     * @return 是否重试成功
     */
    Boolean retryMail(Long logId);
}
