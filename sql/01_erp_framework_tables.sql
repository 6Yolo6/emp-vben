-- ========================================
-- ERP Framework 数据库表结构脚本
-- 基于若依框架规范设计
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS erp_framework CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE erp_framework;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. 组织架构相关表
-- ========================================

-- ----------------------------
-- 组织表（扩展原部门表）
-- ----------------------------
DROP TABLE IF EXISTS `sys_organization`;
CREATE TABLE `sys_organization` (
  `org_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '组织ID',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父组织ID',
  `ancestors` varchar(500) DEFAULT '' COMMENT '祖级列表',
  `org_code` varchar(50) NOT NULL COMMENT '组织编码',
  `org_name` varchar(100) NOT NULL COMMENT '组织名称',
  `org_type` varchar(20) DEFAULT 'DEPARTMENT' COMMENT '组织类型（COMPANY-公司,DEPARTMENT-部门,TEAM-团队）',
  `sort_order` int(11) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `description` text COMMENT '组织描述',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`org_id`),
  UNIQUE KEY `uk_org_code` (`org_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_org_type` (`org_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='组织表';

-- ----------------------------
-- 岗位表（扩展原岗位表）
-- ----------------------------
DROP TABLE IF EXISTS `sys_position`;
CREATE TABLE `sys_position` (
  `position_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `org_id` bigint(20) NOT NULL COMMENT '所属组织ID',
  `position_code` varchar(50) NOT NULL COMMENT '岗位编码',
  `position_name` varchar(100) NOT NULL COMMENT '岗位名称',
  `position_level` varchar(20) DEFAULT NULL COMMENT '岗位级别（P1-P10）',
  `position_type` varchar(20) DEFAULT 'NORMAL' COMMENT '岗位类型（MANAGEMENT-管理岗,TECHNICAL-技术岗,NORMAL-普通岗）',
  `responsibilities` text COMMENT '岗位职责',
  `requirements` text COMMENT '任职要求',
  `sort_order` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`position_id`),
  UNIQUE KEY `uk_position_code` (`position_code`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_position_type` (`position_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- ----------------------------
-- 用户岗位关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_position`;
CREATE TABLE `sys_user_position` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `position_id` bigint(20) NOT NULL COMMENT '岗位ID',
  `is_primary` char(1) DEFAULT '0' COMMENT '是否主岗位（0否 1是）',
  `effective_date` date DEFAULT NULL COMMENT '生效日期',
  `expiry_date` date DEFAULT NULL COMMENT '失效日期',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_position_primary` (`user_id`, `position_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_position_id` (`position_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

-- ----------------------------
-- 用户表（扩展原用户表）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `org_id` bigint(20) DEFAULT NULL COMMENT '主组织ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `user_type` varchar(10) DEFAULT 'EMPLOYEE' COMMENT '用户类型（ADMIN-管理员,EMPLOYEE-员工,EXTERNAL-外部用户）',
  `email` varchar(100) DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(20) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(200) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `employee_no` varchar(50) DEFAULT NULL COMMENT '员工编号',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `entry_date` date DEFAULT NULL COMMENT '入职日期',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用 2锁定）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`),
  UNIQUE KEY `uk_employee_no` (`employee_no`),
  KEY `idx_org_id` (`org_id`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_status` (`status`),
  KEY `idx_phone_number` (`phone_number`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ========================================
-- 2. 权限管理相关表
-- ========================================

-- ----------------------------
-- 角色表（扩展原角色表）
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` varchar(100) NOT NULL COMMENT '角色编码',
  `role_name` varchar(100) NOT NULL COMMENT '角色名称',
  `role_type` varchar(20) DEFAULT 'BUSINESS' COMMENT '角色类型（SYSTEM-系统角色,BUSINESS-业务角色,CUSTOM-自定义角色）',
  `role_sort` int(11) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1全部数据 2自定数据 3本组织数据 4本组织及以下数据 5仅本人数据）',
  `data_scope_org_ids` varchar(500) DEFAULT NULL COMMENT '数据权限组织ID集合',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `org_check_strictly` tinyint(1) DEFAULT '1' COMMENT '组织树选择项是否关联显示',
  `status` char(1) DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_role_type` (`role_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ----------------------------
-- 权限资源表
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `permission_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父权限ID',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `permission_type` varchar(20) NOT NULL COMMENT '权限类型（MENU-菜单,BUTTON-按钮,API-接口）',
  `resource_type` varchar(20) NOT NULL COMMENT '资源类型（MENU-菜单,BUTTON-按钮,API-接口,DATA-数据）',
  `resource_path` varchar(200) DEFAULT NULL COMMENT '资源路径',
  `http_method` varchar(50) DEFAULT NULL COMMENT 'HTTP方法（GET,POST,PUT,DELETE等）',
  `module_name` varchar(50) DEFAULT NULL COMMENT '模块名称',
  `group_name` varchar(50) DEFAULT NULL COMMENT '分组名称',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) DEFAULT '#' COMMENT '图标',
  `sort_order` int(11) DEFAULT '0' COMMENT '显示顺序',
  `is_frame` tinyint(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` tinyint(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `visible` char(1) DEFAULT '0' COMMENT '显示状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '权限状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_resource_type` (`resource_type`),
  KEY `idx_permission_type` (`permission_type`),
  KEY `idx_module_name` (`module_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2000 DEFAULT CHARSET=utf8mb4 COMMENT='权限资源表';

-- ----------------------------
-- 角色权限关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `grant_type` varchar(10) DEFAULT 'GRANT' COMMENT '授权类型（GRANT-授权,DENY-拒绝）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ----------------------------
-- 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

SET FOREIGN_KEY_CHECKS = 1;