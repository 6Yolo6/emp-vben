-- ========================================
-- ERP Framework 基础数据初始化脚本
-- ========================================

USE erp_framework;
SET NAMES utf8mb4;

-- ========================================
-- 初始化组织架构数据
-- ========================================

-- 初始化组织数据
INSERT INTO `sys_organization` (`org_id`, `parent_id`, `ancestors`, `org_code`, `org_name`, `org_type`, `sort_order`, `leader`, `phone`, `email`, `address`, `description`, `status`, `del_flag`, `creator`) VALUES 
(1000, 0, '0', 'ROOT', '立德集团', 'COMPANY', 0, '张总', '400-888-8888', 'admin@lide.com', '深圳市南山区科技园', '立德集团总部', '0', '0', 'admin'),
(1001, 1000, '0,1000', 'SZ_COMPANY', '深圳分公司', 'COMPANY', 1, '李总', '0755-88888888', 'sz@lide.com', '深圳市南山区', '深圳分公司', '0', '0', 'admin'),
(1002, 1000, '0,1000', 'BJ_COMPANY', '北京分公司', 'COMPANY', 2, '王总', '010-88888888', 'bj@lide.com', '北京市朝阳区', '北京分公司', '0', '0', 'admin'),
(1003, 1001, '0,1000,1001', 'SZ_RD_DEPT', '研发部', 'DEPARTMENT', 1, '张经理', '0755-88888801', 'rd@lide.com', '深圳市南山区科技园A座', '负责产品研发', '0', '0', 'admin'),
(1004, 1001, '0,1000,1001', 'SZ_SALES_DEPT', '销售部', 'DEPARTMENT', 2, '刘经理', '0755-88888802', 'sales@lide.com', '深圳市南山区科技园B座', '负责产品销售', '0', '0', 'admin'),
(1005, 1001, '0,1000,1001', 'SZ_HR_DEPT', '人事部', 'DEPARTMENT', 3, '陈经理', '0755-88888803', 'hr@lide.com', '深圳市南山区科技园C座', '负责人力资源管理', '0', '0', 'admin'),
(1006, 1003, '0,1000,1001,1003', 'SZ_RD_JAVA_TEAM', 'Java开发组', 'TEAM', 1, '赵组长', '0755-88888811', 'java@lide.com', '深圳市南山区科技园A座10楼', 'Java后端开发', '0', '0', 'admin'),
(1007, 1003, '0,1000,1001,1003', 'SZ_RD_WEB_TEAM', '前端开发组', 'TEAM', 2, '钱组长', '0755-88888812', 'web@lide.com', '深圳市南山区科技园A座11楼', '前端开发', '0', '0', 'admin');

-- 初始化岗位数据
INSERT INTO `sys_position` (`position_id`, `org_id`, `position_code`, `position_name`, `position_level`, `position_type`, `responsibilities`, `requirements`, `sort_order`, `status`, `del_flag`, `creator`, `remark`) VALUES 
(1000, 1000, 'CEO', '首席执行官', 'P10', 'MANAGEMENT', '负责公司整体战略规划和经营管理', '具有丰富的企业管理经验，MBA学历优先', 1, '0', '0', 'admin', ''),
(1001, 1001, 'GM_SZ', '深圳分公司总经理', 'P9', 'MANAGEMENT', '负责深圳分公司的全面管理工作', '具有分公司管理经验，本科以上学历', 1, '0', '0', 'admin', ''),
(1002, 1003, 'RD_MANAGER', '研发经理', 'P8', 'MANAGEMENT', '负责研发部门的管理和技术规划', '具有技术管理经验，计算机相关专业', 1, '0', '0', 'admin', ''),
(1003, 1006, 'JAVA_LEAD', 'Java技术负责人', 'P7', 'TECHNICAL', '负责Java技术架构设计和团队技术指导', '5年以上Java开发经验，熟悉微服务架构', 1, '0', '0', 'admin', ''),
(1004, 1006, 'JAVA_SENIOR', '高级Java开发工程师', 'P6', 'TECHNICAL', '负责核心业务模块的开发和维护', '3年以上Java开发经验，熟悉Spring生态', 2, '0', '0', 'admin', ''),
(1005, 1006, 'JAVA_DEVELOPER', 'Java开发工程师', 'P5', 'TECHNICAL', '负责业务功能的开发和测试', '1年以上Java开发经验，熟悉基础框架', 3, '0', '0', 'admin', ''),
(1006, 1007, 'WEB_LEAD', '前端技术负责人', 'P7', 'TECHNICAL', '负责前端技术架构和团队管理', '5年以上前端开发经验，熟悉Vue/React', 1, '0', '0', 'admin', ''),
(1007, 1007, 'WEB_DEVELOPER', '前端开发工程师', 'P5', 'TECHNICAL', '负责前端页面开发和交互实现', '2年以上前端开发经验，熟悉ES6+', 2, '0', '0', 'admin', ''),
(1008, 1004, 'SALES_MANAGER', '销售经理', 'P7', 'NORMAL', '负责销售团队管理和客户关系维护', '3年以上销售管理经验，具备良好沟通能力', 1, '0', '0', 'admin', ''),
(1009, 1005, 'HR_SPECIALIST', '人事专员', 'P5', 'NORMAL', '负责招聘、培训、薪酬等人事工作', '人力资源相关专业，熟悉劳动法规', 1, '0', '0', 'admin', '');

-- 初始化用户数据
INSERT INTO `sys_user` (`user_id`, `org_id`, `user_name`, `nick_name`, `real_name`, `user_type`, `email`, `phone_number`, `sex`, `avatar`, `password`, `employee_no`, `birthday`, `entry_date`, `status`, `del_flag`, `creator`, `remark`) VALUES 
(1000, 1000, 'admin', '系统管理员', '管理员', 'ADMIN', 'admin@lide.com', '13800138000', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'EMP001', '1980-01-01', '2020-01-01', '0', '0', 'admin', '系统管理员账号'),
(1001, 1001, 'zhangsan', '张三', '张三', 'EMPLOYEE', 'zhangsan@lide.com', '13800138001', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'EMP002', '1985-03-15', '2021-03-01', '0', '0', 'admin', '深圳分公司总经理'),
(1002, 1003, 'lisi', '李四', '李四', 'EMPLOYEE', 'lisi@lide.com', '13800138002', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'EMP003', '1988-06-20', '2021-06-01', '0', '0', 'admin', '研发经理'),
(1003, 1006, 'wangwu', '王五', '王五', 'EMPLOYEE', 'wangwu@lide.com', '13800138003', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'EMP004', '1990-09-10', '2022-01-01', '0', '0', 'admin', 'Java技术负责人'),
(1004, 1006, 'zhaoliu', '赵六', '赵六', 'EMPLOYEE', 'zhaoliu@lide.com', '13800138004', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'EMP005', '1992-12-05', '2022-03-01', '0', '0', 'admin', 'Java开发工程师');

-- 初始化用户岗位关联数据
INSERT INTO `sys_user_position` (`id`, `user_id`, `position_id`, `is_primary`, `effective_date`, `expiry_date`, `status`, `creator`) VALUES 
(1000, 1000, 1000, '1', '2020-01-01', NULL, '0', 'admin'),
(1001, 1001, 1001, '1', '2021-03-01', NULL, '0', 'admin'),
(1002, 1002, 1002, '1', '2021-06-01', NULL, '0', 'admin'),
(1003, 1003, 1003, '1', '2022-01-01', NULL, '0', 'admin'),
(1004, 1004, 1005, '1', '2022-03-01', NULL, '0', 'admin');

-- ========================================
-- 初始化权限管理数据
-- ========================================

-- 初始化角色数据
INSERT INTO `sys_role` (`role_id`, `role_code`, `role_name`, `role_type`, `role_sort`, `data_scope`, `status`, `del_flag`, `creator`, `remark`) VALUES 
(1000, 'SUPER_ADMIN', '超级管理员', 'SYSTEM', 1, '1', '0', '0', 'admin', '超级管理员，拥有所有权限'),
(1001, 'ADMIN', '系统管理员', 'SYSTEM', 2, '2', '0', '0', 'admin', '系统管理员，拥有系统管理权限'),
(1002, 'MANAGER', '部门经理', 'BUSINESS', 3, '4', '0', '0', 'admin', '部门经理，拥有本部门及以下数据权限'),
(1003, 'EMPLOYEE', '普通员工', 'BUSINESS', 4, '5', '0', '0', 'admin', '普通员工，仅拥有本人数据权限'),
(1004, 'HR_ADMIN', '人事管理员', 'BUSINESS', 5, '1', '0', '0', 'admin', '人事管理员，拥有人事相关权限'),
(1005, 'FINANCE_ADMIN', '财务管理员', 'BUSINESS', 6, '3', '0', '0', 'admin', '财务管理员，拥有财务相关权限');

-- 初始化权限资源数据
INSERT INTO `sys_permission` (`permission_id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `resource_type`, `resource_path`, `module_name`, `group_name`, `component`, `icon`, `sort_order`, `is_frame`, `is_cache`, `visible`, `status`, `del_flag`, `creator`, `remark`) VALUES 
-- 系统管理模块
(2000, 0, 'system', '系统管理', 'MENU', 'MENU', '/system', '系统管理', '系统管理', NULL, 'system', 1, 1, 0, '0', '0', '0', 'admin', '系统管理目录'),
(2001, 2000, 'system:org', '组织管理', 'MENU', 'MENU', '/system/org', '系统管理', '组织管理', 'system/org/index', 'tree', 1, 1, 0, '0', '0', '0', 'admin', '组织管理菜单'),
(2002, 2000, 'system:user', '用户管理', 'MENU', 'MENU', '/system/user', '系统管理', '用户管理', 'system/user/index', 'user', 2, 1, 0, '0', '0', '0', 'admin', '用户管理菜单'),
(2003, 2000, 'system:role', '角色管理', 'MENU', 'MENU', '/system/role', '系统管理', '角色管理', 'system/role/index', 'peoples', 3, 1, 0, '0', '0', '0', 'admin', '角色管理菜单'),
(2004, 2000, 'system:permission', '权限管理', 'MENU', 'MENU', '/system/permission', '系统管理', '权限管理', 'system/permission/index', 'lock', 4, 1, 0, '0', '0', '0', 'admin', '权限管理菜单'),
(2005, 2000, 'system:position', '岗位管理', 'MENU', 'MENU', '/system/position', '系统管理', '岗位管理', 'system/position/index', 'post', 5, 1, 0, '0', '0', '0', 'admin', '岗位管理菜单'),

-- 组织管理权限
(2010, 2001, 'system:org:list', '组织查询', 'BUTTON', 'BUTTON', NULL, '系统管理', '组织管理', NULL, '#', 1, 1, 0, '0', '0', '0', 'admin', ''),
(2011, 2001, 'system:org:add', '组织新增', 'BUTTON', 'BUTTON', NULL, '系统管理', '组织管理', NULL, '#', 2, 1, 0, '0', '0', '0', 'admin', ''),
(2012, 2001, 'system:org:edit', '组织修改', 'BUTTON', 'BUTTON', NULL, '系统管理', '组织管理', NULL, '#', 3, 1, 0, '0', '0', '0', 'admin', ''),
(2013, 2001, 'system:org:remove', '组织删除', 'BUTTON', 'BUTTON', NULL, '系统管理', '组织管理', NULL, '#', 4, 1, 0, '0', '0', '0', 'admin', ''),

-- 用户管理权限
(2020, 2002, 'system:user:list', '用户查询', 'BUTTON', 'BUTTON', NULL, '系统管理', '用户管理', NULL, '#', 1, 1, 0, '0', '0', '0', 'admin', ''),
(2021, 2002, 'system:user:add', '用户新增', 'BUTTON', 'BUTTON', NULL, '系统管理', '用户管理', NULL, '#', 2, 1, 0, '0', '0', '0', 'admin', ''),
(2022, 2002, 'system:user:edit', '用户修改', 'BUTTON', 'BUTTON', NULL, '系统管理', '用户管理', NULL, '#', 3, 1, 0, '0', '0', '0', 'admin', ''),
(2023, 2002, 'system:user:remove', '用户删除', 'BUTTON', 'BUTTON', NULL, '系统管理', '用户管理', NULL, '#', 4, 1, 0, '0', '0', '0', 'admin', ''),
(2024, 2002, 'system:user:resetPwd', '重置密码', 'BUTTON', 'BUTTON', NULL, '系统管理', '用户管理', NULL, '#', 5, 1, 0, '0', '0', '0', 'admin', ''),

-- 角色管理权限
(2030, 2003, 'system:role:list', '角色查询', 'BUTTON', 'BUTTON', NULL, '系统管理', '角色管理', NULL, '#', 1, 1, 0, '0', '0', '0', 'admin', ''),
(2031, 2003, 'system:role:add', '角色新增', 'BUTTON', 'BUTTON', NULL, '系统管理', '角色管理', NULL, '#', 2, 1, 0, '0', '0', '0', 'admin', ''),
(2032, 2003, 'system:role:edit', '角色修改', 'BUTTON', 'BUTTON', NULL, '系统管理', '角色管理', NULL, '#', 3, 1, 0, '0', '0', '0', 'admin', ''),
(2033, 2003, 'system:role:remove', '角色删除', 'BUTTON', 'BUTTON', NULL, '系统管理', '角色管理', NULL, '#', 4, 1, 0, '0', '0', '0', 'admin', '');

-- 初始化超级管理员角色权限（拥有所有权限）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `grant_type`, `creator`)
SELECT 1000, `permission_id`, 'GRANT', 'admin' FROM `sys_permission` WHERE `status` = '0';

-- 初始化用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `creator`) VALUES 
(1000, 1000, 'admin'),
(1001, 1002, 'admin'),
(1002, 1002, 'admin'),
(1003, 1003, 'admin'),
(1004, 1003, 'admin');