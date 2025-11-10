-- 系统文件表
DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_url VARCHAR(500),
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100),
    file_ext VARCHAR(50),
    storage_type SMALLINT DEFAULT 0,
    bucket_name VARCHAR(100),
    object_key VARCHAR(500),
    thumbnail_url VARCHAR(500),
    md5 VARCHAR(64),
    status SMALLINT DEFAULT 1,
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
COMMENT ON TABLE sys_file IS '系统文件表';

-- 添加列注释
COMMENT ON COLUMN sys_file.id IS '主键ID';
COMMENT ON COLUMN sys_file.file_name IS '文件名称';
COMMENT ON COLUMN sys_file.original_name IS '原始文件名';
COMMENT ON COLUMN sys_file.file_path IS '文件路径';
COMMENT ON COLUMN sys_file.file_url IS '文件访问URL';
COMMENT ON COLUMN sys_file.file_size IS '文件大小（字节）';
COMMENT ON COLUMN sys_file.file_type IS '文件类型（MIME类型）';
COMMENT ON COLUMN sys_file.file_ext IS '文件扩展名';
COMMENT ON COLUMN sys_file.storage_type IS '存储类型：0=本地存储 1=MinIO 2=阿里云OSS';
COMMENT ON COLUMN sys_file.bucket_name IS '存储桶名称';
COMMENT ON COLUMN sys_file.object_key IS '对象键（MinIO/OSS）';
COMMENT ON COLUMN sys_file.thumbnail_url IS '缩略图URL（图片文件）';
COMMENT ON COLUMN sys_file.md5 IS '文件MD5值';
COMMENT ON COLUMN sys_file.status IS '状态：0=失败 1=成功';
COMMENT ON COLUMN sys_file.remark IS '备注';
COMMENT ON COLUMN sys_file.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_file.create_by IS '创建人';
COMMENT ON COLUMN sys_file.create_time IS '创建时间';
COMMENT ON COLUMN sys_file.update_by IS '更新人';
COMMENT ON COLUMN sys_file.update_time IS '更新时间';
COMMENT ON COLUMN sys_file.deleted IS '删除标记：0=未删除 1=已删除';

-- 创建索引
CREATE INDEX idx_sys_file_name ON sys_file(file_name);
CREATE INDEX idx_sys_file_type ON sys_file(file_type);
CREATE INDEX idx_sys_file_md5 ON sys_file(md5);
CREATE INDEX idx_sys_file_create_by ON sys_file(create_by);
CREATE INDEX idx_sys_file_tenant_id ON sys_file(tenant_id);
CREATE INDEX idx_sys_file_create_time ON sys_file(create_time);
