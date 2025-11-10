-- 字典类型表
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id BIGSERIAL PRIMARY KEY,
    dict_name VARCHAR(100) NOT NULL,
    dict_type VARCHAR(100) NOT NULL UNIQUE,
    status INTEGER DEFAULT 1,
    remark VARCHAR(500),
    
    -- 租户字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    
    -- 审计字段
    create_by VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

COMMENT ON TABLE sys_dict_type IS '字典类型表';
COMMENT ON COLUMN sys_dict_type.id IS '字典主键';
COMMENT ON COLUMN sys_dict_type.dict_name IS '字典名称';
COMMENT ON COLUMN sys_dict_type.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_type.status IS '状态(0停用 1正常)';
COMMENT ON COLUMN sys_dict_type.remark IS '备注';
COMMENT ON COLUMN sys_dict_type.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_dict_type.create_by IS '创建人';
COMMENT ON COLUMN sys_dict_type.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_type.update_by IS '更新人';
COMMENT ON COLUMN sys_dict_type.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict_type.deleted IS '删除标记(0未删除 1已删除)';

CREATE INDEX idx_dict_type_tenant ON sys_dict_type(tenant_id);
CREATE INDEX idx_dict_type_type ON sys_dict_type(dict_type);

-- 字典数据表
CREATE TABLE IF NOT EXISTS sys_dict_data (
    id BIGSERIAL PRIMARY KEY,
    dict_sort INTEGER DEFAULT 0,
    dict_label VARCHAR(100) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    dict_type VARCHAR(100) NOT NULL,
    css_class VARCHAR(100),
    list_class VARCHAR(100),
    is_default INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    remark VARCHAR(500),
    
    -- 租户字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    
    -- 审计字段
    create_by VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

COMMENT ON TABLE sys_dict_data IS '字典数据表';
COMMENT ON COLUMN sys_dict_data.id IS '字典编码';
COMMENT ON COLUMN sys_dict_data.dict_sort IS '字典排序';
COMMENT ON COLUMN sys_dict_data.dict_label IS '字典标签';
COMMENT ON COLUMN sys_dict_data.dict_value IS '字典键值';
COMMENT ON COLUMN sys_dict_data.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_data.css_class IS '样式属性';
COMMENT ON COLUMN sys_dict_data.list_class IS '表格回显样式';
COMMENT ON COLUMN sys_dict_data.is_default IS '是否默认(0否 1是)';
COMMENT ON COLUMN sys_dict_data.status IS '状态(0停用 1正常)';
COMMENT ON COLUMN sys_dict_data.remark IS '备注';
COMMENT ON COLUMN sys_dict_data.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_dict_data.create_by IS '创建人';
COMMENT ON COLUMN sys_dict_data.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_data.update_by IS '更新人';
COMMENT ON COLUMN sys_dict_data.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict_data.deleted IS '删除标记(0未删除 1已删除)';

CREATE INDEX idx_dict_data_tenant ON sys_dict_data(tenant_id);
CREATE INDEX idx_dict_data_type ON sys_dict_data(dict_type);

-- 插入初始字典类型数据
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, tenant_id) VALUES
('用户性别', 'sys_user_sex', 1, '用户性别列表', 0),
('菜单状态', 'sys_show_hide', 1, '菜单状态列表', 0),
('系统开关', 'sys_normal_disable', 1, '系统开关列表', 0),
('任务状态', 'sys_job_status', 1, '任务状态列表', 0),
('系统是否', 'sys_yes_no', 1, '系统是否列表', 0);

-- 插入初始字典数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, tenant_id) VALUES
(1, '男', '0', 'sys_user_sex', '', '', 1, 1, 0),
(2, '女', '1', 'sys_user_sex', '', '', 0, 1, 0),
(3, '未知', '2', 'sys_user_sex', '', '', 0, 1, 0),

(1, '显示', '0', 'sys_show_hide', '', 'primary', 1, 1, 0),
(2, '隐藏', '1', 'sys_show_hide', '', 'danger', 0, 1, 0),

(1, '正常', '1', 'sys_normal_disable', '', 'primary', 1, 1, 0),
(2, '停用', '0', 'sys_normal_disable', '', 'danger', 0, 1, 0),

(1, '正常', '1', 'sys_job_status', '', 'primary', 1, 1, 0),
(2, '暂停', '0', 'sys_job_status', '', 'danger', 0, 1, 0),

(1, '是', 'Y', 'sys_yes_no', '', 'primary', 1, 1, 0),
(2, '否', 'N', 'sys_yes_no', '', 'danger', 0, 1, 0);
