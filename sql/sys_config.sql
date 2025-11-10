-- 系统参数配置表
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id BIGSERIAL PRIMARY KEY,
    config_name VARCHAR(100) NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT,
    config_type SMALLINT DEFAULT 0,
    remark VARCHAR(500),
    
    -- 租户字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    
    -- 审计字段
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0
);

-- 添加表注释
COMMENT ON TABLE sys_config IS '系统参数配置表';

-- 添加列注释
COMMENT ON COLUMN sys_config.id IS '主键ID';
COMMENT ON COLUMN sys_config.config_name IS '参数名称';
COMMENT ON COLUMN sys_config.config_key IS '参数键名';
COMMENT ON COLUMN sys_config.config_value IS '参数键值';
COMMENT ON COLUMN sys_config.config_type IS '参数类型：0=系统内置 1=用户自定义';
COMMENT ON COLUMN sys_config.remark IS '备注';
COMMENT ON COLUMN sys_config.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_config.create_by IS '创建人';
COMMENT ON COLUMN sys_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_config.update_by IS '更新人';
COMMENT ON COLUMN sys_config.update_time IS '更新时间';
COMMENT ON COLUMN sys_config.deleted IS '删除标记：0=未删除 1=已删除';

-- 创建索引
CREATE UNIQUE INDEX uk_sys_config_key_tenant ON sys_config(config_key, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_sys_config_type ON sys_config(config_type);
CREATE INDEX idx_sys_config_tenant_id ON sys_config(tenant_id);

-- 插入初始数据
INSERT INTO sys_config (config_name, config_key, config_value, config_type, remark, tenant_id) VALUES
('用户初始密码', 'sys.user.initPassword', '123456', 0, '新建用户的默认密码', 0),
('验证码开关', 'sys.account.captchaEnabled', 'true', 0, '是否开启验证码功能（true开启，false关闭）', 0),
('用户注册开关', 'sys.account.registerUser', 'false', 0, '是否开启用户注册功能（true开启，false关闭）', 0),
('登录黑名单', 'sys.login.blackIPList', '', 0, '登录IP黑名单，多个IP用逗号分隔，支持通配符*', 0),
('文件上传大小限制', 'sys.file.maxSize', '10', 0, '文件上传大小限制（MB）', 0),
('会话超时时间', 'sys.session.timeout', '30', 0, '会话超时时间（分钟）', 0);
