-- ========================================
-- ERP Framework 基础数据库初始化脚本
-- 只包含核心表和基础数据
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS erp_framework CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE erp_framework;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. 组织架构相关表
-- ========================================

-- 组织表
DROP TABLE IF EXISTS sys_organization;
CREATE TABLE sys_organization (
  org_id            BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '组织ID',
  parent_id         BIGINT          DEFAULT 0                   COMMENT '父组织ID',
  ancestors         VARCHAR(500)    DEFAULT ''                  COMMENT '祖级列表',
  org_code          VARCHAR(50)     NOT NULL                    COMMENT '组织编码',
  org_name          VARCHAR(100)    NOT NULL                    COMMENT '组织名称',
  org_type          VARCHAR(20)     DEFAULT 'DEPARTMENT'        COMMENT '组织类型',
  sort_order        INT             DEFAULT 0                   COMMENT '显示顺序',
  leader            VARCHAR(50)     DEFAULT NULL                COMMENT '负责人',
  phone             VARCHAR(20)     DEFAULT NULL                COMMENT '联系电话',
  email             VARCHAR(100)    DEFAULT NULL                COMMENT '邮箱',
  address           VARCHAR(200)    DEFAULT NULL                COMMENT '地址',
  description       TEXT            DEFAULT NULL                COMMENT '组织描述',
  status            CHAR(1)         DEFAULT '0'                 COMMENT '状态（0正常 1停用）',
  del_flag          CHAR(1)         DEFAULT '0'                 COMMENT '删除标志',
  creator           VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
  create_time       DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
  updater           VARCHAR(64)     DEFAULT ''                  COMMENT '更新者',
  update_time       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (org_id),
  UNIQUE KEY uk_org_code (org_code)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='组织表';

-- 岗位表
DROP TABLE IF EXISTS sys_position;
CREATE TABLE sys_position (
  position_id       BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '岗位ID',
  org_id            BIGINT          NOT NULL                    COMMENT '所属组织ID',
  position_code     VARCHAR(50)     NOT NULL                    COMMENT '岗位编码',
  position_name     VARCHAR(100)    NOT NULL                    COMMENT '岗位名称',
  position_level    VARCHAR(20)     DEFAULT NULL                COMMENT '岗位级别',
  position_type     VARCHAR(20)     DEFAULT 'NORMAL'            COMMENT '岗位类型',
  responsibilities  TEXT            DEFAULT NULL                COMMENT '岗位职责',
  requirements      TEXT            DEFAULT NULL                COMMENT '任职要求',
  sort_order        INT             DEFAULT 0                   COMMENT '显示顺序',
  status            CHAR(1)         DEFAULT '0'                 COMMENT '状态',
  del_flag          CHAR(1)         DEFAULT '0'                 COMMENT '删除标志',
  creator           VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
  create_time       DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
  updater           VARCHAR(64)     DEFAULT ''                  COMMENT '更新者',
  update_time       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark            VARCHAR(500)    DEFAULT NULL                COMMENT '备注',
  PRIMARY KEY (position_id),
  UNIQUE KEY uk_position_code (position_code)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='岗位表';

-- ========================================
-- 2. 用户管理相关表
-- ========================================

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  user_id           BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '用户ID',
  org_id            BIGINT          DEFAULT NULL                COMMENT '组织ID',
  username          VARCHAR(50)     NOT NULL                    COMMENT '用户名',
  nickname          VARCHAR(50)     DEFAULT NULL                COMMENT '昵称',
  email             VARCHAR(100)    DEFAULT NULL                COMMENT '邮箱',
  phone_number      VARCHAR(20)     DEFAULT NULL                COMMENT '手机号',
  sex               CHAR(1)         DEFAULT '0'                 COMMENT '性别',
  avatar            VARCHAR(200)    DEFAULT NULL                COMMENT '头像',
  password          VARCHAR(100)    DEFAULT NULL                COMMENT '密码',
  status            CHAR(1)         DEFAULT '0'                 COMMENT '状态',
  del_flag          CHAR(1)         DEFAULT '0'                 COMMENT '删除标志',
  login_ip          VARCHAR(128)    DEFAULT NULL                COMMENT '最后登录IP',
  login_date        DATETIME        DEFAULT NULL                COMMENT '最后登录时间',
  creator           VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
  create_time       DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
  updater           VARCHAR(64)     DEFAULT ''                  COMMENT '更新者',
  update_time       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark            VARCHAR(500)    DEFAULT NULL                COMMENT '备注',
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='用户表';

-- ========================================
-- 3. 权限管理相关表
-- ========================================

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  role_id               BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '角色ID',
  role_code             VARCHAR(100)    NOT NULL                    COMMENT '角色编码',
  role_name             VARCHAR(100)    NOT NULL                    COMMENT '角色名称',
  role_type             VARCHAR(20)     DEFAULT 'BUSINESS'          COMMENT '角色类型',
  role_sort             INT             NOT NULL                    COMMENT '显示顺序',
  data_scope            CHAR(1)         DEFAULT '1'                 COMMENT '数据范围',
  data_scope_org_ids    VARCHAR(500)    DEFAULT NULL                COMMENT '数据权限组织ID集合',
  menu_check_strictly   TINYINT(1)      DEFAULT 1                   COMMENT '菜单树选择项是否关联显示',
  org_check_strictly    TINYINT(1)      DEFAULT 1                   COMMENT '组织树选择项是否关联显示',
  status                CHAR(1)         DEFAULT '0'                 COMMENT '角色状态',
  del_flag              CHAR(1)         DEFAULT '0'                 COMMENT '删除标志',
  creator               VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
  create_time           DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
  updater               VARCHAR(64)     DEFAULT ''                  COMMENT '更新者',
  update_time           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark                VARCHAR(500)    DEFAULT NULL                COMMENT '备注',
  PRIMARY KEY (role_id),
  UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='角色信息表';

-- 权限资源表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
  permission_id         BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '权限ID',
  parent_id             BIGINT          DEFAULT 0                   COMMENT '父权限ID',
  permission_code       VARCHAR(100)    NOT NULL                    COMMENT '权限编码',
  permission_name       VARCHAR(100)    NOT NULL                    COMMENT '权限名称',
  permission_type       VARCHAR(20)     NOT NULL                    COMMENT '权限类型',
  resource_type         VARCHAR(20)     NOT NULL                    COMMENT '资源类型',
  resource_path         VARCHAR(200)    DEFAULT NULL                COMMENT '资源路径',
  http_method           VARCHAR(50)     DEFAULT NULL                COMMENT 'HTTP方法',
  module_name           VARCHAR(50)     DEFAULT NULL                COMMENT '模块名称',
  group_name            VARCHAR(50)     DEFAULT NULL                COMMENT '分组名称',
  component             VARCHAR(255)    DEFAULT NULL                COMMENT '组件路径',
  icon                  VARCHAR(100)    DEFAULT '#'                 COMMENT '图标',
  sort_order            INT             DEFAULT 0                   COMMENT '显示顺序',
  is_frame              TINYINT(1)      DEFAULT 1                   COMMENT '是否为外链',
  is_cache              TINYINT(1)      DEFAULT 0                   COMMENT '是否缓存',
  visible               CHAR(1)         DEFAULT '0'                 COMMENT '显示状态',
  status                CHAR(1)         DEFAULT '0'                 COMMENT '权限状态',
  del_flag              CHAR(1)         DEFAULT '0'                 COMMENT '删除标志',
  creator               VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
  create_time           DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
  updater               VARCHAR(64)     DEFAULT ''                  COMMENT '更新者',
  update_time           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  remark                VARCHAR(500)    DEFAULT ''                  COMMENT '备注',
  PRIMARY KEY (permission_id),
  UNIQUE KEY uk_permission_code (permission_code)
) ENGINE=InnoDB AUTO_INCREMENT=2000 COMMENT='权限资源表';

-- 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
  id                    BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '主键ID',
  role_id               BIGINT          NOT NULL                    COMMENT '角色ID',
  permission_id         BIGINT          NOT NULL                    COMMENT '权限ID',
  grant_type            VARCHAR(10)     DEFAULT 'GRANT'             COMMENT '授权类型',
  creator               VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
  create_time           DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='角色权限关联表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  id                    BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '主键ID',
  user_id               BIGINT          NOT NULL                    COMMENT '用户ID',
  role_id               BIGINT          NOT NULL                    COMMENT '角色ID',
  creator               VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
  create_time           DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='用户角色关联表';

-- ========================================
-- 4. 初始化基础数据
-- ========================================

-- 初始化组织数据
INSERT INTO sys_organization (org_id, parent_id, ancestors, org_code, org_name, org_type, sort_order, leader, status, creator, create_time) VALUES
(1000, 0, '0', 'ROOT', '总公司', 'COMPANY', 0, 'admin', '0', 'admin', NOW()),
(1001, 1000, '0,1000', 'TECH', '技术部', 'DEPARTMENT', 1, 'tech_leader', '0', 'admin', NOW()),
(1002, 1000, '0,1000', 'HR', '人事部', 'DEPARTMENT', 2, 'hr_leader', '0', 'admin', NOW()),
(1003, 1000, '0,1000', 'FINANCE', '财务部', 'DEPARTMENT', 3, 'finance_leader', '0', 'admin', NOW());

-- 初始化岗位数据
INSERT INTO sys_position (position_id, org_id, position_code, position_name, position_level, position_type, sort_order, status, creator, create_time) VALUES
(1000, 1001, 'DEV', '开发工程师', 'P5', 'TECHNICAL', 1, '0', 'admin', NOW()),
(1001, 1001, 'SENIOR_DEV', '高级开发工程师', 'P6', 'TECHNICAL', 2, '0', 'admin', NOW()),
(1002, 1002, 'HR_SPECIALIST', '人事专员', 'P4', 'NORMAL', 1, '0', 'admin', NOW()),
(1003, 1003, 'ACCOUNTANT', '会计', 'P4', 'NORMAL', 1, '0', 'admin', NOW());

-- 初始化角色数据
INSERT INTO sys_role (role_id, role_code, role_name, role_type, role_sort, data_scope, status, creator, create_time, remark) VALUES
(1000, 'SUPER_ADMIN', '超级管理员', 'SYSTEM', 1, '1', '0', 'admin', NOW(), '超级管理员，拥有所有权限'),
(1001, 'ADMIN', '系统管理员', 'SYSTEM', 2, '2', '0', 'admin', NOW(), '系统管理员，拥有系统管理权限'),
(1002, 'MANAGER', '部门经理', 'BUSINESS', 3, '4', '0', 'admin', NOW(), '部门经理，拥有本部门及以下数据权限'),
(1003, 'EMPLOYEE', '普通员工', 'BUSINESS', 4, '5', '0', 'admin', NOW(), '普通员工，仅拥有本人数据权限');

-- 初始化权限资源数据
INSERT INTO sys_permission (permission_id, parent_id, permission_code, permission_name, permission_type, resource_type, resource_path, module_name, group_name, icon, sort_order, status, creator, create_time, remark) VALUES
-- 系统管理模块
(2000, 0, 'system', '系统管理', 'MENU', 'MENU', '/system', '系统管理', '系统管理', 'system', 1, '0', 'admin', NOW(), '系统管理目录'),
(2001, 2000, 'system:org', '组织管理', 'MENU', 'MENU', '/system/org', '系统管理', '组织管理', 'tree', 1, '0', 'admin', NOW(), '组织管理菜单'),
(2002, 2000, 'system:user', '用户管理', 'MENU', 'MENU', '/system/user', '系统管理', '用户管理', 'user', 2, '0', 'admin', NOW(), '用户管理菜单'),
(2003, 2000, 'system:role', '角色管理', 'MENU', 'MENU', '/system/role', '系统管理', '角色管理', 'peoples', 3, '0', 'admin', NOW(), '角色管理菜单'),
(2004, 2000, 'system:permission', '权限管理', 'MENU', 'MENU', '/system/permission', '系统管理', '权限管理', 'lock', 4, '0', 'admin', NOW(), '权限管理菜单'),

-- 组织管理权限
(2010, 2001, 'system:org:list', '组织查询', 'BUTTON', 'BUTTON', NULL, '系统管理', '组织管理', '#', 1, '0', 'admin', NOW(), ''),
(2011, 2001, 'system:org:add', '组织新增', 'BUTTON', 'BUTTON', NULL, '系统管理', '组织管理', '#', 2, '0', 'admin', NOW(), ''),
(2012, 2001, 'system:org:edit', '组织修改', 'BUTTON', 'BUTTON', NULL, '系统管理', '组织管理', '#', 3, '0', 'admin', NOW(), ''),
(2013, 2001, 'system:org:remove', '组织删除', 'BUTTON', 'BUTTON', NULL, '系统管理', '组织管理', '#', 4, '0', 'admin', NOW(), ''),

-- 用户管理权限
(2020, 2002, 'system:user:list', '用户查询', 'BUTTON', 'BUTTON', NULL, '系统管理', '用户管理', '#', 1, '0', 'admin', NOW(), ''),
(2021, 2002, 'system:user:add', '用户新增', 'BUTTON', 'BUTTON', NULL, '系统管理', '用户管理', '#', 2, '0', 'admin', NOW(), ''),
(2022, 2002, 'system:user:edit', '用户修改', 'BUTTON', 'BUTTON', NULL, '系统管理', '用户管理', '#', 3, '0', 'admin', NOW(), ''),
(2023, 2002, 'system:user:remove', '用户删除', 'BUTTON', 'BUTTON', NULL, '系统管理', '用户管理', '#', 4, '0', 'admin', NOW(), '');

-- 初始化用户数据
INSERT INTO sys_user (user_id, org_id, username, nickname, email, phone_number, sex, password, status, creator, create_time, remark) VALUES
(1000, 1000, 'admin', '系统管理员', 'admin@example.com', '13800138000', '1', '$2a$10$7JB720yubVSOfvam/l0.dOOiE.dMOY4qV5ovEWEpTYlZIBjqmu8I2', '0', 'admin', NOW(), '系统管理员'),
(1001, 1001, 'zhangsan', '张三', 'zhangsan@example.com', '13800138001', '1', '$2a$10$7JB720yubVSOfvam/l0.dOOiE.dMOY4qV5ovEWEpTYlZIBjqmu8I2', '0', 'admin', NOW(), '开发工程师'),
(1002, 1002, 'lisi', '李四', 'lisi@example.com', '13800138002', '0', '$2a$10$7JB720yubVSOfvam/l0.dOOiE.dMOY4qV5ovEWEpTYlZIBjqmu8I2', '0', 'admin', NOW(), '人事专员');

-- 初始化用户角色关联
INSERT INTO sys_user_role (user_id, role_id, creator, create_time) VALUES
(1000, 1000, 'admin', NOW()),  -- admin用户：超级管理员
(1001, 1003, 'admin', NOW()),  -- zhangsan用户：普通员工
(1002, 1002, 'admin', NOW());  -- lisi用户：部门经理

-- 初始化角色权限关联（超级管理员拥有所有权限）
INSERT INTO sys_role_permission (role_id, permission_id, creator, create_time) VALUES
(1000, 2000, 'admin', NOW()),
(1000, 2001, 'admin', NOW()),
(1000, 2002, 'admin', NOW()),
(1000, 2003, 'admin', NOW()),
(1000, 2004, 'admin', NOW()),
(1000, 2010, 'admin', NOW()),
(1000, 2011, 'admin', NOW()),
(1000, 2012, 'admin', NOW()),
(1000, 2013, 'admin', NOW()),
(1000, 2020, 'admin', NOW()),
(1000, 2021, 'admin', NOW()),
(1000, 2022, 'admin', NOW()),
(1000, 2023, 'admin', NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- 完成
SELECT 'ERP Framework 基础数据库初始化完成！' AS message;
