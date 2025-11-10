-- ========================================
-- EMP Platform 多租户改造脚本
-- 支持一个用户属于多个单位
-- ========================================

SET client_encoding = 'UTF8';

-- ========================================
-- 1. 创建租户表
-- ========================================
DROP TABLE IF EXISTS sys_tenant CASCADE;
CREATE TABLE sys_tenant (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_code     VARCHAR(50)     UNIQUE NOT NULL,
    tenant_name     VARCHAR(100)    NOT NULL,
    tenant_type     VARCHAR(20)     DEFAULT 'company',
    contact_name    VARCHAR(50),
    contact_phone   VARCHAR(20),
    contact_email   VARCHAR(100),
    domain          VARCHAR(200),
    logo            VARCHAR(500),
    expire_time     TIMESTAMP,
    account_limit   INTEGER         DEFAULT 100,
    storage_limit   BIGINT          DEFAULT 10737418240,
    status          SMALLINT        DEFAULT 1,
    deleted         SMALLINT        DEFAULT 0,
    remark          VARCHAR(500),
    create_by       BIGINT,
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_by       BIGINT,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_tenant_status CHECK (status IN (0, 1)),
    CONSTRAINT chk_tenant_deleted CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE sys_tenant IS '租户(单位)表';
COMMENT ON COLUMN sys_tenant.tenant_code IS '租户编码,用于URL识别';
COMMENT ON COLUMN sys_tenant.tenant_name IS '租户名称';
COMMENT ON COLUMN sys_tenant.tenant_type IS '租户类型: company-公司, group-集团, branch-分支机构';
COMMENT ON COLUMN sys_tenant.domain IS '独立域名';
COMMENT ON COLUMN sys_tenant.expire_time IS '租户过期时间';
COMMENT ON COLUMN sys_tenant.account_limit IS '账号数量限制';
COMMENT ON COLUMN sys_tenant.storage_limit IS '存储空间限制(字节)';

CREATE INDEX idx_tenant_code ON sys_tenant(tenant_code);
CREATE INDEX idx_tenant_status ON sys_tenant(status);

-- ========================================
-- 2. 创建用户租户关联表
-- ========================================
DROP TABLE IF EXISTS sys_user_tenant CASCADE;
CREATE TABLE sys_user_tenant (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    tenant_id       BIGINT          NOT NULL,
    is_primary      BOOLEAN         DEFAULT FALSE,
    join_time       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    status          SMALLINT        DEFAULT 1,

    CONSTRAINT uk_user_tenant UNIQUE (user_id, tenant_id),
    CONSTRAINT chk_user_tenant_status CHECK (status IN (0, 1))
);

COMMENT ON TABLE sys_user_tenant IS '用户租户关联表';
COMMENT ON COLUMN sys_user_tenant.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_tenant.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_tenant.is_primary IS '是否为用户的主要单位';
COMMENT ON COLUMN sys_user_tenant.join_time IS '用户加入该单位的时间';
COMMENT ON COLUMN sys_user_tenant.status IS '状态(0停用 1正常)';

CREATE INDEX idx_user_tenant_user_id ON sys_user_tenant(user_id);
CREATE INDEX idx_user_tenant_tenant_id ON sys_user_tenant(tenant_id);
CREATE INDEX idx_user_tenant_is_primary ON sys_user_tenant(is_primary);

-- ========================================
-- 3. 为现有表添加租户字段
-- ========================================

-- 用户表添加默认租户ID
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS default_tenant_id BIGINT;
COMMENT ON COLUMN sys_user.default_tenant_id IS '用户默认登录的租户ID';

-- 部门表添加租户ID
ALTER TABLE sys_dept ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;
CREATE INDEX IF NOT EXISTS idx_dept_tenant_id ON sys_dept(tenant_id);
COMMENT ON COLUMN sys_dept.tenant_id IS '所属租户ID';

-- 岗位表添加租户ID
ALTER TABLE sys_post ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;
CREATE INDEX IF NOT EXISTS idx_post_tenant_id ON sys_post(tenant_id);
COMMENT ON COLUMN sys_post.tenant_id IS '所属租户ID';

-- 角色表添加租户ID
ALTER TABLE sys_role ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;
CREATE INDEX IF NOT EXISTS idx_role_tenant_id ON sys_role(tenant_id);
COMMENT ON COLUMN sys_role.tenant_id IS '所属租户ID';

-- 菜单表添加租户ID(NULL表示系统菜单)
ALTER TABLE sys_menu ADD COLUMN IF NOT EXISTS tenant_id BIGINT;
CREATE INDEX IF NOT EXISTS idx_menu_tenant_id ON sys_menu(tenant_id);
COMMENT ON COLUMN sys_menu.tenant_id IS '所属租户ID,NULL表示系统菜单(所有租户共享)';

-- ========================================
-- 4. 修改关联表主键(加入租户ID)
-- ========================================

-- 用户岗位关联表
ALTER TABLE sys_user_post ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;
ALTER TABLE sys_user_post DROP CONSTRAINT IF EXISTS sys_user_post_pkey;
ALTER TABLE sys_user_post ADD CONSTRAINT sys_user_post_pkey PRIMARY KEY (user_id, post_id, tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_post_tenant_id ON sys_user_post(tenant_id);
COMMENT ON COLUMN sys_user_post.tenant_id IS '所属租户ID';

-- 用户角色关联表
ALTER TABLE sys_user_role ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;
ALTER TABLE sys_user_role DROP CONSTRAINT IF EXISTS sys_user_role_pkey;
ALTER TABLE sys_user_role ADD CONSTRAINT sys_user_role_pkey PRIMARY KEY (user_id, role_id, tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_role_tenant_id ON sys_user_role(tenant_id);
COMMENT ON COLUMN sys_user_role.tenant_id IS '所属租户ID';

-- 岗位角色关联表
ALTER TABLE sys_post_role ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;
ALTER TABLE sys_post_role DROP CONSTRAINT IF EXISTS sys_post_role_pkey;
ALTER TABLE sys_post_role ADD CONSTRAINT sys_post_role_pkey PRIMARY KEY (post_id, role_id, tenant_id);
CREATE INDEX IF NOT EXISTS idx_post_role_tenant_id ON sys_post_role(tenant_id);
COMMENT ON COLUMN sys_post_role.tenant_id IS '所属租户ID';

-- 角色菜单关联表
ALTER TABLE sys_role_menu ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;
ALTER TABLE sys_role_menu DROP CONSTRAINT IF EXISTS sys_role_menu_pkey;
ALTER TABLE sys_role_menu ADD CONSTRAINT sys_role_menu_pkey PRIMARY KEY (role_id, menu_id, tenant_id);
CREATE INDEX IF NOT EXISTS idx_role_menu_tenant_id ON sys_role_menu(tenant_id);
COMMENT ON COLUMN sys_role_menu.tenant_id IS '所属租户ID';

-- 角色部门关联表
ALTER TABLE sys_role_dept ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;
ALTER TABLE sys_role_dept DROP CONSTRAINT IF EXISTS sys_role_dept_pkey;
ALTER TABLE sys_role_dept ADD CONSTRAINT sys_role_dept_pkey PRIMARY KEY (role_id, dept_id, tenant_id);
CREATE INDEX IF NOT EXISTS idx_role_dept_tenant_id ON sys_role_dept(tenant_id);
COMMENT ON COLUMN sys_role_dept.tenant_id IS '所属租户ID';

-- ========================================
-- 5. 初始化默认租户
-- ========================================
INSERT INTO sys_tenant (id, tenant_code, tenant_name, tenant_type, status, remark)
VALUES (1, 'default', '默认单位', 'company', 1, '系统默认租户')
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- 6. 数据迁移
-- ========================================

-- 将现有数据关联到默认租户
UPDATE sys_dept SET tenant_id = 1 WHERE tenant_id = 0;
UPDATE sys_post SET tenant_id = 1 WHERE tenant_id = 0;
UPDATE sys_role SET tenant_id = 1 WHERE tenant_id = 0;
UPDATE sys_user_post SET tenant_id = 1 WHERE tenant_id = 0;
UPDATE sys_user_role SET tenant_id = 1 WHERE tenant_id = 0;
UPDATE sys_post_role SET tenant_id = 1 WHERE tenant_id = 0;
UPDATE sys_role_menu SET tenant_id = 1 WHERE tenant_id = 0;
UPDATE sys_role_dept SET tenant_id = 1 WHERE tenant_id = 0;

-- 将现有用户关联到默认租户
INSERT INTO sys_user_tenant (user_id, tenant_id, is_primary, status)
SELECT id, 1, TRUE, 1
FROM sys_user
WHERE deleted = 0
ON CONFLICT (user_id, tenant_id) DO NOTHING;

-- 设置用户的默认租户
UPDATE sys_user SET default_tenant_id = 1 WHERE default_tenant_id IS NULL;

-- ========================================
-- 7. 完成提示
-- ========================================
DO $$
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE '多租户改造完成！';
    RAISE NOTICE '========================================';
    RAISE NOTICE '已创建表:';
    RAISE NOTICE '  - sys_tenant (租户表)';
    RAISE NOTICE '  - sys_user_tenant (用户租户关联表)';
    RAISE NOTICE '';
    RAISE NOTICE '已修改表(添加tenant_id字段):';
    RAISE NOTICE '  - sys_dept';
    RAISE NOTICE '  - sys_post';
    RAISE NOTICE '  - sys_role';
    RAISE NOTICE '  - sys_menu';
    RAISE NOTICE '  - sys_user_post';
    RAISE NOTICE '  - sys_user_role';
    RAISE NOTICE '  - sys_post_role';
    RAISE NOTICE '  - sys_role_menu';
    RAISE NOTICE '  - sys_role_dept';
    RAISE NOTICE '';
    RAISE NOTICE '已创建默认租户: default';
    RAISE NOTICE '已将现有数据迁移到默认租户';
    RAISE NOTICE '========================================';
END $$;
