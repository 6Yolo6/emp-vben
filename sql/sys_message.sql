-- 站内消息表
CREATE TABLE sys_message (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    message_type SMALLINT NOT NULL DEFAULT 1,
    sender_id BIGINT,
    sender_name VARCHAR(100),
    receiver_type SMALLINT NOT NULL DEFAULT 1,
    receiver_ids TEXT,
    receiver_role_ids TEXT,
    status SMALLINT NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_by VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE sys_message IS '站内消息表';
COMMENT ON COLUMN sys_message.id IS '消息ID';
COMMENT ON COLUMN sys_message.title IS '消息标题';
COMMENT ON COLUMN sys_message.content IS '消息内容';
COMMENT ON COLUMN sys_message.message_type IS '消息类型：1-系统通知 2-业务消息 3-预警消息';
COMMENT ON COLUMN sys_message.sender_id IS '发送人ID';
COMMENT ON COLUMN sys_message.sender_name IS '发送人姓名';
COMMENT ON COLUMN sys_message.receiver_type IS '接收人类型：1-指定用户 2-指定角色 3-全体用户';
COMMENT ON COLUMN sys_message.receiver_ids IS '接收人ID列表，逗号分隔';
COMMENT ON COLUMN sys_message.receiver_role_ids IS '接收角色ID列表，逗号分隔';
COMMENT ON COLUMN sys_message.status IS '状态：0-草稿 1-已发送';
COMMENT ON COLUMN sys_message.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_message.create_by IS '创建人';
COMMENT ON COLUMN sys_message.create_time IS '创建时间';
COMMENT ON COLUMN sys_message.update_by IS '更新人';
COMMENT ON COLUMN sys_message.update_time IS '更新时间';
COMMENT ON COLUMN sys_message.deleted IS '删除标记：0-未删除 1-已删除';

-- 创建索引
CREATE INDEX idx_message_tenant ON sys_message(tenant_id);
CREATE INDEX idx_message_sender ON sys_message(sender_id);
CREATE INDEX idx_message_status ON sys_message(status);
CREATE INDEX idx_message_create_time ON sys_message(create_time);

-- 用户消息关联表（记录每个用户的消息阅读状态）
CREATE TABLE sys_user_message (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_read SMALLINT NOT NULL DEFAULT 0,
    read_time TIMESTAMP,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE sys_user_message IS '用户消息关联表';
COMMENT ON COLUMN sys_user_message.id IS '主键ID';
COMMENT ON COLUMN sys_user_message.message_id IS '消息ID';
COMMENT ON COLUMN sys_user_message.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_message.is_read IS '是否已读：0-未读 1-已读';
COMMENT ON COLUMN sys_user_message.read_time IS '阅读时间';
COMMENT ON COLUMN sys_user_message.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_message.create_time IS '创建时间';
COMMENT ON COLUMN sys_user_message.deleted IS '删除标记：0-未删除 1-已删除';

-- 创建索引
CREATE INDEX idx_user_message_message ON sys_user_message(message_id);
CREATE INDEX idx_user_message_user ON sys_user_message(user_id);
CREATE INDEX idx_user_message_read ON sys_user_message(is_read);
CREATE INDEX idx_user_message_tenant ON sys_user_message(tenant_id);
CREATE UNIQUE INDEX uk_user_message ON sys_user_message(message_id, user_id, tenant_id) WHERE deleted = 0;

-- 消息模板表
CREATE TABLE sys_message_template (
    id BIGSERIAL PRIMARY KEY,
    template_code VARCHAR(100) NOT NULL,
    template_name VARCHAR(200) NOT NULL,
    template_type SMALLINT NOT NULL DEFAULT 1,
    title_template VARCHAR(500) NOT NULL,
    content_template TEXT NOT NULL,
    variables TEXT,
    description VARCHAR(500),
    status SMALLINT NOT NULL DEFAULT 1,
    version INT NOT NULL DEFAULT 1,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_by VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE sys_message_template IS '消息模板表';
COMMENT ON COLUMN sys_message_template.id IS '模板ID';
COMMENT ON COLUMN sys_message_template.template_code IS '模板编码';
COMMENT ON COLUMN sys_message_template.template_name IS '模板名称';
COMMENT ON COLUMN sys_message_template.template_type IS '模板类型：1-系统通知 2-业务消息 3-预警消息';
COMMENT ON COLUMN sys_message_template.title_template IS '标题模板';
COMMENT ON COLUMN sys_message_template.content_template IS '内容模板';
COMMENT ON COLUMN sys_message_template.variables IS '变量定义，JSON格式';
COMMENT ON COLUMN sys_message_template.description IS '模板描述';
COMMENT ON COLUMN sys_message_template.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN sys_message_template.version IS '版本号';
COMMENT ON COLUMN sys_message_template.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_message_template.create_by IS '创建人';
COMMENT ON COLUMN sys_message_template.create_time IS '创建时间';
COMMENT ON COLUMN sys_message_template.update_by IS '更新人';
COMMENT ON COLUMN sys_message_template.update_time IS '更新时间';
COMMENT ON COLUMN sys_message_template.deleted IS '删除标记：0-未删除 1-已删除';

-- 创建索引
CREATE UNIQUE INDEX uk_template_code ON sys_message_template(template_code, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_template_type ON sys_message_template(template_type);
CREATE INDEX idx_template_status ON sys_message_template(status);
CREATE INDEX idx_template_tenant ON sys_message_template(tenant_id);

-- 插入初始消息模板
INSERT INTO sys_message_template (template_code, template_name, template_type, title_template, content_template, variables, description, status, tenant_id)
VALUES 
('USER_REGISTER', '用户注册通知', 1, '欢迎注册', '尊敬的 ${userName}，您已成功注册！', '{"userName":"用户名"}', '用户注册成功通知', 1, 0),
('PASSWORD_RESET', '密码重置通知', 1, '密码重置成功', '您的密码已重置，新密码为：${newPassword}', '{"newPassword":"新密码"}', '密码重置通知', 1, 0),
('TASK_ASSIGN', '任务分配通知', 2, '新任务分配', '您有新的任务：${taskName}，请及时处理', '{"taskName":"任务名称"}', '任务分配通知', 1, 0);
