-- ========================================
-- EMP Platform 核心数据库表结构脚本 (PostgreSQL)
-- 基于岗位的RBAC权限模型
-- ========================================

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- ========================================
-- 1. 部门表 (sys_dept)
-- ========================================
DROP TABLE IF EXISTS sys_dept CASCADE;
CREATE TABLE sys_dept (
    id              BIGSERIAL       PRIMARY KEY,
    parent_id       BIGINT          DEFAULT 0                   NOT NULL,
    ancestors       VARCHAR(500)    DEFAULT ''                  NOT NULL,
    dept_name       VARCHAR(50)     NOT NULL,
    order_num       INTEGER         DEFAULT 0                   NOT NULL,
    leader          VARCHAR(50),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    status          SMALLINT        DEFAULT 1                   NOT NULL,
    deleted         SMALLINT        DEFAULT 0                   NOT NULL,
    remark          VARCHAR(500),
    create_by       BIGINT,
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    update_by       BIGINT,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    CONSTRAINT chk_dept_status CHECK (status IN (0, 1)),
    CONSTRAINT chk_dept_deleted CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.id IS '部门ID';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_dept.ancestors IS '祖级列表';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN sys_dept.order_num IS '显示顺序';
COMMENT ON COLUMN sys_dept.leader IS '负责人';
COMMENT ON COLUMN sys_dept.phone IS '联系电话';
COMMENT ON COLUMN sys_dept.email IS '邮箱';
COMMENT ON COLUMN sys_dept.status IS '状态(0停用 1正常)';
COMMENT ON COLUMN sys_dept.deleted IS '删除标志(0未删除 1已删除)';
COMMENT ON COLUMN sys_dept.remark IS '备注';
COMMENT ON COLUMN sys_dept.create_by IS '创建人';
COMMENT ON COLUMN sys_dept.create_time IS '创建时间';
COMMENT ON COLUMN sys_dept.update_by IS '更新人';
COMMENT ON COLUMN sys_dept.update_time IS '更新时间';

CREATE INDEX idx_dept_parent_id ON sys_dept(parent_id);
CREATE INDEX idx_dept_status ON sys_dept(status);
CREATE INDEX idx_dept_deleted ON sys_dept(deleted);

-- ========================================
-- 2. 岗位表 (sys_post)
-- ========================================
DROP TABLE IF EXISTS sys_post CASCADE;
CREATE TABLE sys_post (
    id              BIGSERIAL       PRIMARY KEY,
    post_code       VARCHAR(50)     NOT NULL,
    post_name       VARCHAR(50)     NOT NULL,
    post_sort       INTEGER         DEFAULT 0                   NOT NULL,
    status          SMALLINT        DEFAULT 1                   NOT NULL,
    deleted         SMALLINT        DEFAULT 0                   NOT NULL,
    remark          VARCHAR(500),
    create_by       BIGINT,
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    update_by       BIGINT,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    CONSTRAINT uk_post_code UNIQUE (post_code),
    CONSTRAINT chk_post_status CHECK (status IN (0, 1)),
    CONSTRAINT chk_post_deleted CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE sys_post IS '岗位表';
COMMENT ON COLUMN sys_post.id IS '岗位ID';
COMMENT ON COLUMN sys_post.post_code IS '岗位编码';
COMMENT ON COLUMN sys_post.post_name IS '岗位名称';
COMMENT ON COLUMN sys_post.post_sort IS '显示顺序';
COMMENT ON COLUMN sys_post.status IS '状态(0停用 1正常)';
COMMENT ON COLUMN sys_post.deleted IS '删除标志(0未删除 1已删除)';
COMMENT ON COLUMN sys_post.remark IS '备注';
COMMENT ON COLUMN sys_post.create_by IS '创建人';
COMMENT ON COLUMN sys_post.create_time IS '创建时间';
COMMENT ON COLUMN sys_post.update_by IS '更新人';
COMMENT ON COLUMN sys_post.update_time IS '更新时间';

CREATE INDEX idx_post_status ON sys_post(status);
CREATE INDEX idx_post_deleted ON sys_post(deleted);

-- ========================================
-- 3. 用户表 (sys_user)
-- ========================================
DROP TABLE IF EXISTS sys_user CASCADE;
CREATE TABLE sys_user (
    id              BIGSERIAL       PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL,
    nickname        VARCHAR(50),
    email           VARCHAR(100),
    mobile          VARCHAR(20),
    avatar          VARCHAR(500),
    password        VARCHAR(100)    NOT NULL,
    status          SMALLINT        DEFAULT 1                   NOT NULL,
    dept_id         BIGINT,
    deleted         SMALLINT        DEFAULT 0                   NOT NULL,
    login_ip        VARCHAR(128),
    login_date      TIMESTAMP,
    create_by       BIGINT,
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    update_by       BIGINT,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    remark          VARCHAR(500),
    CONSTRAINT uk_username UNIQUE (username),
    CONSTRAINT chk_user_status CHECK (status IN (0, 1)),
    CONSTRAINT chk_user_deleted CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '用户ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.nickname IS '昵称';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.mobile IS '手机号';
COMMENT ON COLUMN sys_user.avatar IS '头像地址';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.status IS '状态(0停用 1正常)';
COMMENT ON COLUMN sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN sys_user.deleted IS '删除标志(0未删除 1已删除)';
COMMENT ON COLUMN sys_user.login_ip IS '最后登录IP';
COMMENT ON COLUMN sys_user.login_date IS '最后登录时间';
COMMENT ON COLUMN sys_user.create_by IS '创建人';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_by IS '更新人';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';
COMMENT ON COLUMN sys_user.remark IS '备注';

CREATE INDEX idx_user_username ON sys_user(username);
CREATE INDEX idx_user_dept_id ON sys_user(dept_id);
CREATE INDEX idx_user_status ON sys_user(status);
CREATE INDEX idx_user_deleted ON sys_user(deleted);

-- ========================================
-- 4. 角色表 (sys_role)
-- ========================================
DROP TABLE IF EXISTS sys_role CASCADE;
CREATE TABLE sys_role (
    id                      BIGSERIAL       PRIMARY KEY,
    role_name               VARCHAR(50)     NOT NULL,
    role_key                VARCHAR(50)     NOT NULL,
    role_sort               INTEGER         DEFAULT 0                   NOT NULL,
    data_scope              SMALLINT        DEFAULT 1                   NOT NULL,
    menu_check_strictly     BOOLEAN         DEFAULT TRUE                NOT NULL,
    dept_check_strictly     BOOLEAN         DEFAULT TRUE                NOT NULL,
    status                  SMALLINT        DEFAULT 1                   NOT NULL,
    deleted                 SMALLINT        DEFAULT 0                   NOT NULL,
    remark                  VARCHAR(500),
    create_by               BIGINT,
    create_time             TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    update_by               BIGINT,
    update_time             TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    CONSTRAINT uk_role_key UNIQUE (role_key),
    CONSTRAINT chk_role_status CHECK (status IN (0, 1)),
    CONSTRAINT chk_role_deleted CHECK (deleted IN (0, 1)),
    CONSTRAINT chk_role_data_scope CHECK (data_scope IN (1, 2, 3, 4, 5))
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '角色ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_key IS '角色权限标识';
COMMENT ON COLUMN sys_role.role_sort IS '显示顺序';
COMMENT ON COLUMN sys_role.data_scope IS '数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)';
COMMENT ON COLUMN sys_role.menu_check_strictly IS '菜单树选择项是否关联显示(0父子不互相关联显示 1父子互相关联显示)';
COMMENT ON COLUMN sys_role.dept_check_strictly IS '部门树选择项是否关联显示(0父子不互相关联显示 1父子互相关联显示)';
COMMENT ON COLUMN sys_role.status IS '状态(0停用 1正常)';
COMMENT ON COLUMN sys_role.deleted IS '删除标志(0未删除 1已删除)';
COMMENT ON COLUMN sys_role.remark IS '备注';
COMMENT ON COLUMN sys_role.create_by IS '创建人';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_by IS '更新人';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';

CREATE INDEX idx_role_status ON sys_role(status);
CREATE INDEX idx_role_deleted ON sys_role(deleted);

-- ========================================
-- 5. 菜单表 (sys_menu)
-- ========================================
DROP TABLE IF EXISTS sys_menu CASCADE;
CREATE TABLE sys_menu (
    id              BIGSERIAL       PRIMARY KEY,
    parent_id       BIGINT          DEFAULT 0                   NOT NULL,
    menu_name       VARCHAR(50)     NOT NULL,
    menu_type       CHAR(1)         NOT NULL,
    path            VARCHAR(200),
    component       VARCHAR(200),
    perms           VARCHAR(100),
    icon            VARCHAR(100),
    order_num       INTEGER         DEFAULT 0                   NOT NULL,
    visible         SMALLINT        DEFAULT 1                   NOT NULL,
    status          SMALLINT        DEFAULT 1                   NOT NULL,
    deleted         SMALLINT        DEFAULT 0                   NOT NULL,
    create_by       BIGINT,
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    update_by       BIGINT,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    CONSTRAINT chk_menu_type CHECK (menu_type IN ('M', 'C', 'F')),
    CONSTRAINT chk_menu_visible CHECK (visible IN (0, 1)),
    CONSTRAINT chk_menu_status CHECK (status IN (0, 1)),
    CONSTRAINT chk_menu_deleted CHECK (deleted IN (0, 1))
);

COMMENT ON TABLE sys_menu IS '菜单表';
COMMENT ON COLUMN sys_menu.id IS '菜单ID';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型(M目录 C菜单 F按钮)';
COMMENT ON COLUMN sys_menu.path IS '路由地址';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.perms IS '权限标识';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.order_num IS '显示顺序';
COMMENT ON COLUMN sys_menu.visible IS '显示状态(0隐藏 1显示)';
COMMENT ON COLUMN sys_menu.status IS '状态(0停用 1正常)';
COMMENT ON COLUMN sys_menu.deleted IS '删除标志(0未删除 1已删除)';
COMMENT ON COLUMN sys_menu.create_by IS '创建人';
COMMENT ON COLUMN sys_menu.create_time IS '创建时间';
COMMENT ON COLUMN sys_menu.update_by IS '更新人';
COMMENT ON COLUMN sys_menu.update_time IS '更新时间';

CREATE INDEX idx_menu_parent_id ON sys_menu(parent_id);
CREATE INDEX idx_menu_status ON sys_menu(status);
CREATE INDEX idx_menu_deleted ON sys_menu(deleted);

-- ========================================
-- 6. 用户角色关联表 (sys_user_role)
-- ========================================
DROP TABLE IF EXISTS sys_user_role CASCADE;
CREATE TABLE sys_user_role (
    user_id         BIGINT          NOT NULL,
    role_id         BIGINT          NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';

CREATE INDEX idx_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_user_role_role_id ON sys_user_role(role_id);

-- ========================================
-- 7. 用户岗位关联表 (sys_user_post)
-- ========================================
DROP TABLE IF EXISTS sys_user_post CASCADE;
CREATE TABLE sys_user_post (
    user_id         BIGINT          NOT NULL,
    post_id         BIGINT          NOT NULL,
    PRIMARY KEY (user_id, post_id)
);

COMMENT ON TABLE sys_user_post IS '用户岗位关联表';
COMMENT ON COLUMN sys_user_post.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_post.post_id IS '岗位ID';

CREATE INDEX idx_user_post_user_id ON sys_user_post(user_id);
CREATE INDEX idx_user_post_post_id ON sys_user_post(post_id);

-- ========================================
-- 8. 岗位角色关联表 (sys_post_role)
-- ========================================
DROP TABLE IF EXISTS sys_post_role CASCADE;
CREATE TABLE sys_post_role (
    post_id         BIGINT          NOT NULL,
    role_id         BIGINT          NOT NULL,
    PRIMARY KEY (post_id, role_id)
);

COMMENT ON TABLE sys_post_role IS '岗位角色关联表';
COMMENT ON COLUMN sys_post_role.post_id IS '岗位ID';
COMMENT ON COLUMN sys_post_role.role_id IS '角色ID';

CREATE INDEX idx_post_role_post_id ON sys_post_role(post_id);
CREATE INDEX idx_post_role_role_id ON sys_post_role(role_id);

-- ========================================
-- 9. 角色菜单关联表 (sys_role_menu)
-- ========================================
DROP TABLE IF EXISTS sys_role_menu CASCADE;
CREATE TABLE sys_role_menu (
    role_id         BIGINT          NOT NULL,
    menu_id         BIGINT          NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色菜单关联表';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单ID';

CREATE INDEX idx_role_menu_role_id ON sys_role_menu(role_id);
CREATE INDEX idx_role_menu_menu_id ON sys_role_menu(menu_id);

-- ========================================
-- 完成提示
-- ========================================
DO $$
BEGIN
    RAISE NOTICE 'EMP Platform 核心数据库表创建完成！';
    RAISE NOTICE '已创建9张核心表：';
    RAISE NOTICE '1. sys_dept - 部门表';
    RAISE NOTICE '2. sys_post - 岗位表';
    RAISE NOTICE '3. sys_user - 用户表';
    RAISE NOTICE '4. sys_role - 角色表';
    RAISE NOTICE '5. sys_menu - 菜单表';
    RAISE NOTICE '6. sys_user_role - 用户角色关联表';
    RAISE NOTICE '7. sys_user_post - 用户岗位关联表';
    RAISE NOTICE '8. sys_post_role - 岗位角色关联表';
    RAISE NOTICE '9. sys_role_menu - 角色菜单关联表';
END $$;
