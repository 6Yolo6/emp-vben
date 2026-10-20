package com.ldjt.emp.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.mail.MailSendDTO;
import com.ldjt.emp.dto.mail.MailTemplateDTO;
import com.ldjt.emp.entity.SysMailConfig;
import com.ldjt.emp.entity.SysMailLog;
import com.ldjt.emp.entity.SysMailTemplate;
import com.ldjt.emp.mapper.SysMailConfigMapper;
import com.ldjt.emp.mapper.SysMailLogMapper;
import com.ldjt.emp.mapper.SysMailTemplateMapper;
import com.ldjt.emp.service.SysMailService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;

import static com.ldjt.emp.entity.table.SysMailConfigTableDef.SYS_MAIL_CONFIG;
import static com.ldjt.emp.entity.table.SysMailTemplateTableDef.SYS_MAIL_TEMPLATE;

/**
 * 邮件服务实现类
 *
 * @author kiro
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMailServiceImpl implements SysMailService {

    private final SysMailConfigMapper mailConfigMapper;
    private final SysMailLogMapper mailLogMapper;
    private final SysMailTemplateMapper mailTemplateMapper;
    private final TemplateEngine templateEngine;

    @Override
    public Boolean sendMail(MailSendDTO mailSendDTO) {
        // 获取邮件配置
        SysMailConfig config = getMailConfig(mailSendDTO.getConfigId());

        // 创建邮件日志
        SysMailLog mailLog = createMailLog(mailSendDTO, config.getId());

        try {
            // 发送邮件
            doSendMail(config, mailSendDTO);

            // 更新日志为成功
            mailLog.setSendStatus(1);
            mailLog.setSendTime(LocalDateTime.now());
            mailLogMapper.insert(mailLog);

            log.info("邮件发送成功，收件人：{}", String.join(",", mailSendDTO.getToEmails()));
            return true;
        } catch (Exception e) {
            // 更新日志为失败
            mailLog.setSendStatus(2);
            mailLog.setErrorMessage(e.getMessage());
            mailLog.setSendTime(LocalDateTime.now());
            mailLogMapper.insert(mailLog);

            log.error("邮件发送失败，收件人：{}，错误：{}", String.join(",", mailSendDTO.getToEmails()), e.getMessage(), e);
            throw new BusinessException("邮件发送失败：" + e.getMessage());
        }
    }

    @Override
    public Boolean sendTemplateMail(MailTemplateDTO mailTemplateDTO) {
        // 查询模板
        SysMailTemplate template = mailTemplateMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(SYS_MAIL_TEMPLATE.TEMPLATE_CODE.eq(mailTemplateDTO.getTemplateCode()))
                        .and(SYS_MAIL_TEMPLATE.STATUS.eq(1))
        );

        if (template == null) {
            throw new BusinessException("邮件模板不存在或已停用");
        }

        // 渲染模板
        String content = renderTemplate(template.getContent(), mailTemplateDTO.getVariables());

        // 构建邮件发送DTO
        MailSendDTO mailSendDTO = new MailSendDTO();
        mailSendDTO.setToEmails(mailTemplateDTO.getToEmails());
        mailSendDTO.setCcEmails(mailTemplateDTO.getCcEmails());
        mailSendDTO.setBccEmails(mailTemplateDTO.getBccEmails());
        mailSendDTO.setSubject(template.getSubject());
        mailSendDTO.setContent(content);
        mailSendDTO.setIsHtml(template.getTemplateType() == 1);
        mailSendDTO.setAttachments(mailTemplateDTO.getAttachments());
        mailSendDTO.setConfigId(mailTemplateDTO.getConfigId());

        return sendMail(mailSendDTO);
    }

    @Async
    @Override
    public void sendMailAsync(MailSendDTO mailSendDTO) {
        sendMail(mailSendDTO);
    }

    @Override
    public Boolean retryMail(Long logId) {
        // 查询邮件日志
        SysMailLog mailLog = mailLogMapper.selectOneById(logId);
        if (mailLog == null) {
            throw new BusinessException("邮件日志不存在");
        }

        if (mailLog.getSendStatus() == 1) {
            throw new BusinessException("邮件已发送成功，无需重试");
        }

        // 检查重试次数
        if (mailLog.getRetryCount() >= 3) {
            throw new BusinessException("邮件重试次数已达上限");
        }

        // 构建邮件发送DTO
        MailSendDTO mailSendDTO = new MailSendDTO();
        mailSendDTO.setToEmails(StrUtil.split(mailLog.getToEmails(), ','));
        if (StrUtil.isNotBlank(mailLog.getCcEmails())) {
            mailSendDTO.setCcEmails(StrUtil.split(mailLog.getCcEmails(), ','));
        }
        if (StrUtil.isNotBlank(mailLog.getBccEmails())) {
            mailSendDTO.setBccEmails(StrUtil.split(mailLog.getBccEmails(), ','));
        }
        mailSendDTO.setSubject(mailLog.getSubject());
        mailSendDTO.setContent(mailLog.getContent());
        mailSendDTO.setConfigId(mailLog.getConfigId());

        try {
            // 获取邮件配置
            SysMailConfig config = getMailConfig(mailLog.getConfigId());

            // 发送邮件
            doSendMail(config, mailSendDTO);

            // 更新日志
            mailLog.setSendStatus(1);
            mailLog.setRetryCount(mailLog.getRetryCount() + 1);
            mailLog.setSendTime(LocalDateTime.now());
            mailLog.setErrorMessage(null);
            mailLogMapper.update(mailLog);

            log.info("邮件重试发送成功，日志ID：{}", logId);
            return true;
        } catch (Exception e) {
            // 更新重试次数和错误信息
            mailLog.setRetryCount(mailLog.getRetryCount() + 1);
            mailLog.setErrorMessage(e.getMessage());
            mailLogMapper.update(mailLog);

            log.error("邮件重试发送失败，日志ID：{}，错误：{}", logId, e.getMessage(), e);
            throw new BusinessException("邮件重试发送失败：" + e.getMessage());
        }
    }

    /**
     * 获取邮件配置
     */
    private SysMailConfig getMailConfig(Long configId) {
        SysMailConfig config;
        if (configId != null) {
            config = mailConfigMapper.selectOneById(configId);
        } else {
            // 获取默认配置
            config = mailConfigMapper.selectOneByQuery(
                    QueryWrapper.create()
                            .where(SYS_MAIL_CONFIG.IS_DEFAULT.eq(true))
                            .and(SYS_MAIL_CONFIG.STATUS.eq(1))
            );
        }

        if (config == null) {
            throw new BusinessException("邮件配置不存在或已停用");
        }

        return config;
    }

    /**
     * 创建邮件日志
     */
    private SysMailLog createMailLog(MailSendDTO mailSendDTO, Long configId) {
        SysMailLog mailLog = new SysMailLog();
        mailLog.setConfigId(configId);
        mailLog.setToEmails(String.join(",", mailSendDTO.getToEmails()));
        if (CollUtil.isNotEmpty(mailSendDTO.getCcEmails())) {
            mailLog.setCcEmails(String.join(",", mailSendDTO.getCcEmails()));
        }
        if (CollUtil.isNotEmpty(mailSendDTO.getBccEmails())) {
            mailLog.setBccEmails(String.join(",", mailSendDTO.getBccEmails()));
        }
        mailLog.setSubject(mailSendDTO.getSubject());
        mailLog.setContent(mailSendDTO.getContent());
        if (CollUtil.isNotEmpty(mailSendDTO.getAttachments())) {
            mailLog.setAttachments(String.join(",", mailSendDTO.getAttachments()));
        }
        mailLog.setSendStatus(0);
        mailLog.setRetryCount(0);
        return mailLog;
    }

    /**
     * 执行邮件发送
     */
    private void doSendMail(SysMailConfig config, MailSendDTO mailSendDTO) throws MessagingException {
        // 创建邮件发送器
        JavaMailSenderImpl mailSender = createMailSender(config);

        // 创建邮件消息
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // 设置发件人
        String fromName = StrUtil.isNotBlank(config.getFromName()) ? config.getFromName() : config.getUsername();
        helper.setFrom(config.getUsername(), fromName);

        // 设置收件人
        helper.setTo(mailSendDTO.getToEmails().toArray(new String[0]));

        // 设置抄送人
        if (CollUtil.isNotEmpty(mailSendDTO.getCcEmails())) {
            helper.setCc(mailSendDTO.getCcEmails().toArray(new String[0]));
        }

        // 设置密送人
        if (CollUtil.isNotEmpty(mailSendDTO.getBccEmails())) {
            helper.setBcc(mailSendDTO.getBccEmails().toArray(new String[0]));
        }

        // 设置主题
        helper.setSubject(mailSendDTO.getSubject());

        // 设置内容
        helper.setText(mailSendDTO.getContent(), mailSendDTO.getIsHtml());

        // 添加附件
        if (CollUtil.isNotEmpty(mailSendDTO.getAttachments())) {
            for (String attachment : mailSendDTO.getAttachments()) {
                File file = new File(attachment);
                if (file.exists()) {
                    helper.addAttachment(file.getName(), file);
                }
            }
        }

        // 发送邮件
        mailSender.send(mimeMessage);
    }

    /**
     * 创建邮件发送器
     */
    private JavaMailSenderImpl createMailSender(SysMailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getSmtpHost());
        mailSender.setPort(config.getSmtpPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());
        mailSender.setDefaultEncoding("UTF-8");

        // 设置邮件属性
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.timeout", "25000");

        if (config.getSslEnable()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", config.getSmtpPort());
        }

        mailSender.setJavaMailProperties(props);
        return mailSender;
    }

    /**
     * 渲染模板
     */
    private String renderTemplate(String templateContent, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            context.setVariables(variables);
        }
        return templateEngine.process(templateContent, context);
    }
}
