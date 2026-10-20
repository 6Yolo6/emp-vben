-- 邮件配置表
DROP TABLE IF EXISTS sys_mail_config;
CREATE TABLE sys_mail_config (
    id BIGSERIAL PRIMARY KEY,
    config_name VARCHAR(100) NOT NULL,
    smtp_host VARCHAR(100) NOT NULL,
    smtp_port INT NOT NULL DEFAULT 587,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(200) NOT NULL,
    from_name VARCHAR(100),
    ssl_enable SMALLINT DEFAULT 1,
    is_default SMALLINT DEFAULT 0,
    status SMALLINT DEFAULT 1,
    -- 租户字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    -- 审计字段
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE sys_mail_config IS '邮件配置表';
COMMENT ON COLUMN sys_mail_config.id IS '配置ID';
COMMENT ON COLUMN sys_mail_config.config_name IS '配置名称';
COMMENT ON COLUMN sys_mail_config.smtp_host IS 'SMTP服务器';
COMMENT ON COLUMN sys_mail_config.smtp_port IS 'SMTP端口';
COMMENT ON COLUMN sys_mail_config.username IS '用户名';
COMMENT ON COLUMN sys_mail_config.password IS '密码(加密)';
COMMENT ON COLUMN sys_mail_config.from_name IS '发件人名称';
COMMENT ON COLUMN sys_mail_config.ssl_enable IS '是否启用SSL：0=禁用 1=启用';
COMMENT ON COLUMN sys_mail_config.is_default IS '是否默认配置：0=否 1=是';
COMMENT ON COLUMN sys_mail_config.status IS '状态：0=停用 1=正常';
COMMENT ON COLUMN sys_mail_config.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_mail_config.create_by IS '创建人';
COMMENT ON COLUMN sys_mail_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_mail_config.update_by IS '更新人';
COMMENT ON COLUMN sys_mail_config.update_time IS '更新时间';
COMMENT ON COLUMN sys_mail_config.deleted IS '删除标记：0=未删除 1=已删除';

CREATE INDEX idx_mail_config_tenant ON sys_mail_config(tenant_id);
CREATE INDEX idx_mail_config_default ON sys_mail_config(is_default);

-- 邮件发送记录表
DROP TABLE IF EXISTS sys_mail_log;
CREATE TABLE sys_mail_log (
    id BIGSERIAL PRIMARY KEY,
    config_id BIGINT,
    template_id BIGINT,
    to_emails TEXT NOT NULL,
    cc_emails TEXT,
    bcc_emails TEXT,
    subject VARCHAR(200) NOT NULL,
    content TEXT,
    attachments TEXT,
    send_status SMALLINT DEFAULT 0,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    send_time TIMESTAMP,
    -- 租户字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    -- 审计字段
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_mail_log IS '邮件发送记录表';
COMMENT ON COLUMN sys_mail_log.id IS '日志ID';
COMMENT ON COLUMN sys_mail_log.config_id IS '邮件配置ID';
COMMENT ON COLUMN sys_mail_log.template_id IS '模板ID';
COMMENT ON COLUMN sys_mail_log.to_emails IS '收件人';
COMMENT ON COLUMN sys_mail_log.cc_emails IS '抄送人';
COMMENT ON COLUMN sys_mail_log.bcc_emails IS '密送人';
COMMENT ON COLUMN sys_mail_log.subject IS '邮件主题';
COMMENT ON COLUMN sys_mail_log.content IS '邮件内容';
COMMENT ON COLUMN sys_mail_log.attachments IS '附件信息';
COMMENT ON COLUMN sys_mail_log.send_status IS '发送状态：0=待发送 1=成功 2=失败';
COMMENT ON COLUMN sys_mail_log.error_message IS '错误信息';
COMMENT ON COLUMN sys_mail_log.retry_count IS '重试次数';
COMMENT ON COLUMN sys_mail_log.send_time IS '发送时间';
COMMENT ON COLUMN sys_mail_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_mail_log.create_by IS '创建人';
COMMENT ON COLUMN sys_mail_log.create_time IS '创建时间';
COMMENT ON COLUMN sys_mail_log.update_by IS '更新人';
COMMENT ON COLUMN sys_mail_log.update_time IS '更新时间';

CREATE INDEX idx_mail_log_config ON sys_mail_log(config_id);
CREATE INDEX idx_mail_log_status ON sys_mail_log(send_status);
CREATE INDEX idx_mail_log_tenant ON sys_mail_log(tenant_id);
CREATE INDEX idx_mail_log_send_time ON sys_mail_log(send_time);

-- 邮件模板表
DROP TABLE IF EXISTS sys_mail_template;
CREATE TABLE sys_mail_template (
    id BIGSERIAL PRIMARY KEY,
    template_code VARCHAR(100) NOT NULL,
    template_name VARCHAR(200) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    template_type SMALLINT DEFAULT 1,
    variables TEXT,
    status SMALLINT DEFAULT 1,
    -- 租户字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    -- 审计字段
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE sys_mail_template IS '邮件模板表';
COMMENT ON COLUMN sys_mail_template.id IS '模板ID';
COMMENT ON COLUMN sys_mail_template.template_code IS '模板编码';
COMMENT ON COLUMN sys_mail_template.template_name IS '模板名称';
COMMENT ON COLUMN sys_mail_template.subject IS '邮件主题';
COMMENT ON COLUMN sys_mail_template.content IS '邮件内容';
COMMENT ON COLUMN sys_mail_template.template_type IS '模板类型：1=HTML 2=纯文本';
COMMENT ON COLUMN sys_mail_template.variables IS '模板变量';
COMMENT ON COLUMN sys_mail_template.status IS '状态：0=停用 1=正常';
COMMENT ON COLUMN sys_mail_template.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_mail_template.create_by IS '创建人';
COMMENT ON COLUMN sys_mail_template.create_time IS '创建时间';
COMMENT ON COLUMN sys_mail_template.update_by IS '更新人';
COMMENT ON COLUMN sys_mail_template.update_time IS '更新时间';
COMMENT ON COLUMN sys_mail_template.deleted IS '删除标记：0=未删除 1=已删除';

CREATE UNIQUE INDEX uk_mail_template_code ON sys_mail_template(template_code, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_mail_template_tenant ON sys_mail_template(tenant_id);

-- 插入默认邮件配置（使用你提供的163邮箱配置）
INSERT INTO sys_mail_config (config_name, smtp_host, smtp_port, username, password, from_name, ssl_enable, is_default, status, tenant_id)
VALUES ('163邮箱', 'smtp.163.com', 465, 'rcjf6186@163.com', 'XLT3cjXgkTdQKTYY', '系统管理员', 1, 1, 1, 0);

-- 插入示例邮件模板
INSERT INTO sys_mail_template (template_code, template_name, subject, content, template_type, variables, status, tenant_id)
VALUES 
('USER_REGISTER', '用户注册通知', '欢迎注册', 
'<h2>欢迎您，[(${username})]！</h2><p>您已成功注册我们的系统。</p><p>您的账号：[(${account})]</p><p>请妥善保管您的账号信息。</p>', 
1, 'username,account', 1, 0),

('PASSWORD_RESET', '密码重置通知', '密码重置成功', 
'<h2>密码重置通知</h2><p>尊敬的 [(${username})]：</p><p>您的密码已成功重置。</p><p>如非本人操作，请立即联系管理员。</p>', 
1, 'username', 1, 0),

('SYSTEM_NOTICE', '系统通知', '系统通知', 
'<h2>系统通知</h2><p>[(${content})]</p><p>通知时间：[(${time})]</p>', 
1, 'content,time', 1, 0);
