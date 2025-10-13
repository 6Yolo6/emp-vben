-- ========================================
-- EMP Platform 核心数据初始化脚本 (PostgreSQL)
-- 包含超级管理员、基础菜单、基础岗位
-- ========================================

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- ========================================
-- 1. 初始化部门数据
-- ========================================
INSERT INTO sys_dept (id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, deleted, create_by, create_time) VALUES
(1, 0, '0', '利德集团', 0, '张总', '400-888-8888', 'admin@lide.com', 1, 0, 1, CURRENT_TIMESTAMP),
(2, 1, '0,1', '组织部', 1, '李总', '0755-88888888', 'sz@lide.com', 1, 0, 1, CURRENT_TIMESTAMP),
(3, 1, '0,1', '安监部', 2, '王总', '010-88888888', 'bj@lide.com', 1, 0, 1, CURRENT_TIMESTAMP),
(4, 2, '0,1,2', '研发部', 1, '张经理', '0755-88888801', 'rd@lide.com', 1, 0, 1, CURRENT_TIMESTAMP),
(5, 2, '0,1,2', '销售部', 2, '刘经理', '0755-88888802', 'sales@lide.com', 1, 0, 1, CURRENT_TIMESTAMP),
(6, 2, '0,1,2', '人事部', 3, '陈经理', '0755-88888803', 'hr@lide.com', 1, 0, 1, CURRENT_TIMESTAMP);

-- 重置序列
SELECT setval('sys_dept_id_seq', (SELECT MAX(id) FROM sys_dept));

-- ========================================
-- 2. 初始化岗位数据
-- ========================================
INSERT INTO sys_post (id, post_code, post_name, post_sort, status, deleted, remark, create_by, create_time) VALUES
(1, 'CEO', '首席执行官', 1, 1, 0, '公司最高管理者', 1, CURRENT_TIMESTAMP),
(2, 'CTO', '首席技术官', 2, 1, 0, '技术负责人', 1, CURRENT_TIMESTAMP),
(3, 'PM', '项目经理', 3, 1, 0, '项目管理', 1, CURRENT_TIMESTAMP),
(4, 'DEV', '开发工程师', 4, 1, 0, '软件开发', 1, CURRENT_TIMESTAMP),
(5, 'QA', '测试工程师', 5, 1, 0, '软件测试', 1, CURRENT_TIMESTAMP),
(6, 'HR', '人事专员', 6, 1, 0, '人力资源', 1, CURRENT_TIMESTAMP),
(7, 'SALES', '销售专员', 7, 1, 0, '产品销售', 1, CURRENT_TIMESTAMP);

-- 重置序列
SELECT setval('sys_post_id_seq', (SELECT MAX(id) FROM sys_post));

-- ========================================
-- 3. 初始化角色数据
-- ========================================
INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, status, deleted, remark, create_by, create_time) VALUES
(1, '超级管理员', 'admin', 1, 1, 1, 0, '超级管理员，拥有所有权限', 1, CURRENT_TIMESTAMP),
(2, '系统管理员', 'system', 2, 1, 1, 0, '系统管理员，拥有系统管理权限', 1, CURRENT_TIMESTAMP),
(3, '部门经理', 'manager', 3, 4, 1, 0, '部门经理，拥有本部门及以下数据权限', 1, CURRENT_TIMESTAMP),
(4, '普通员工', 'employee', 4, 5, 1, 0, '普通员工，仅拥有本人数据权限', 1, CURRENT_TIMESTAMP);

-- 重置序列
SELECT setval('sys_role_id_seq', (SELECT MAX(id) FROM sys_role));

-- ========================================
-- 4. 初始化菜单数据
-- ========================================
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, order_num, visible, status, deleted, create_by, create_time) VALUES
-- 一级菜单
(1, 0, '系统管理', 'M', '/system', NULL, NULL, 'system', 1, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(2, 0, '系统监控', 'M', '/monitor', NULL, NULL, 'monitor', 2, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(3, 0, '系统工具', 'M', '/tool', NULL, NULL, 'tool', 3, 1, 1, 0, 1, CURRENT_TIMESTAMP),

-- 系统管理子菜单
(100, 1, '用户管理', 'C', '/system/user', 'system/user/index', 'system:user:list', 'user', 1, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(101, 1, '角色管理', 'C', '/system/role', 'system/role/index', 'system:role:list', 'peoples', 2, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(102, 1, '菜单管理', 'C', '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 3, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(103, 1, '部门管理', 'C', '/system/dept', 'system/dept/index', 'system:dept:list', 'tree', 4, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(104, 1, '岗位管理', 'C', '/system/post', 'system/post/index', 'system:post:list', 'post', 5, 1, 1, 0, 1, CURRENT_TIMESTAMP),

-- 用户管理按钮
(1000, 100, '用户查询', 'F', NULL, NULL, 'system:user:query', '#', 1, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1001, 100, '用户新增', 'F', NULL, NULL, 'system:user:add', '#', 2, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1002, 100, '用户修改', 'F', NULL, NULL, 'system:user:edit', '#', 3, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1003, 100, '用户删除', 'F', NULL, NULL, 'system:user:remove', '#', 4, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1004, 100, '重置密码', 'F', NULL, NULL, 'system:user:resetPwd', '#', 5, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1005, 100, '用户导出', 'F', NULL, NULL, 'system:user:export', '#', 6, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1006, 100, '用户导入', 'F', NULL, NULL, 'system:user:import', '#', 7, 1, 1, 0, 1, CURRENT_TIMESTAMP),

-- 角色管理按钮
(1010, 101, '角色查询', 'F', NULL, NULL, 'system:role:query', '#', 1, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1011, 101, '角色新增', 'F', NULL, NULL, 'system:role:add', '#', 2, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1012, 101, '角色修改', 'F', NULL, NULL, 'system:role:edit', '#', 3, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1013, 101, '角色删除', 'F', NULL, NULL, 'system:role:remove', '#', 4, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1014, 101, '分配权限', 'F', NULL, NULL, 'system:role:auth', '#', 5, 1, 1, 0, 1, CURRENT_TIMESTAMP),

-- 菜单管理按钮
(1020, 102, '菜单查询', 'F', NULL, NULL, 'system:menu:query', '#', 1, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1021, 102, '菜单新增', 'F', NULL, NULL, 'system:menu:add', '#', 2, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1022, 102, '菜单修改', 'F', NULL, NULL, 'system:menu:edit', '#', 3, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1023, 102, '菜单删除', 'F', NULL, NULL, 'system:menu:remove', '#', 4, 1, 1, 0, 1, CURRENT_TIMESTAMP),

-- 部门管理按钮
(1030, 103, '部门查询', 'F', NULL, NULL, 'system:dept:query', '#', 1, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1031, 103, '部门新增', 'F', NULL, NULL, 'system:dept:add', '#', 2, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1032, 103, '部门修改', 'F', NULL, NULL, 'system:dept:edit', '#', 3, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1033, 103, '部门删除', 'F', NULL, NULL, 'system:dept:remove', '#', 4, 1, 1, 0, 1, CURRENT_TIMESTAMP),

-- 岗位管理按钮
(1040, 104, '岗位查询', 'F', NULL, NULL, 'system:post:query', '#', 1, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1041, 104, '岗位新增', 'F', NULL, NULL, 'system:post:add', '#', 2, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1042, 104, '岗位修改', 'F', NULL, NULL, 'system:post:edit', '#', 3, 1, 1, 0, 1, CURRENT_TIMESTAMP),
(1043, 104, '岗位删除', 'F', NULL, NULL, 'system:post:remove', '#', 4, 1, 1, 0, 1, CURRENT_TIMESTAMP);

-- 重置序列
SELECT setval('sys_menu_id_seq', (SELECT MAX(id) FROM sys_menu));

-- ========================================
-- 5. 初始化用户数据
-- ========================================
-- 密码: admin123 (BCrypt加密)
INSERT INTO sys_user (id, username, nickname, email, mobile, avatar, password, status, dept_id, deleted, create_by, create_time, remark) VALUES
(1, 'admin', '超级管理员', 'admin@lide.com', '13800138000', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1, 1, 0, 1, CURRENT_TIMESTAMP, '超级管理员账号'),
(2, 'zhangsan', '张三', 'zhangsan@lide.com', '13800138001', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1, 4, 0, 1, CURRENT_TIMESTAMP, '研发部员工'),
(3, 'lisi', '李四', 'lisi@lide.com', '13800138002', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 1, 5, 0, 1, CURRENT_TIMESTAMP, '销售部员工');

-- 重置序列
SELECT setval('sys_user_id_seq', (SELECT MAX(id) FROM sys_user));

-- ========================================
-- 6. 初始化用户角色关联
-- ========================================
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- admin: 超级管理员
(2, 4),  -- zhangsan: 普通员工
(3, 4);  -- lisi: 普通员工

-- ========================================
-- 7. 初始化用户岗位关联
-- ========================================
INSERT INTO sys_user_post (user_id, post_id) VALUES
(1, 1),  -- admin: CEO
(2, 4),  -- zhangsan: 开发工程师
(3, 7);  -- lisi: 销售专员

-- ========================================
-- 8. 初始化岗位角色关联
-- ========================================
INSERT INTO sys_post_role (post_id, role_id) VALUES
(1, 1),  -- CEO: 超级管理员
(2, 2),  -- CTO: 系统管理员
(3, 3),  -- PM: 部门经理
(4, 4),  -- DEV: 普通员工
(5, 4),  -- QA: 普通员工
(6, 4),  -- HR: 普通员工
(7, 4);  -- SALES: 普通员工

-- ========================================
-- 9. 初始化角色菜单关联(超级管理员拥有所有菜单权限)
-- ========================================
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE status = 1 AND deleted = 0;

-- ========================================
-- 完成提示
-- ========================================
DO $$
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'EMP Platform 核心数据初始化完成！';
    RAISE NOTICE '========================================';
    RAISE NOTICE '初始化数据统计：';
    RAISE NOTICE '- 部门: % 个', (SELECT COUNT(*) FROM sys_dept);
    RAISE NOTICE '- 岗位: % 个', (SELECT COUNT(*) FROM sys_post);
    RAISE NOTICE '- 角色: % 个', (SELECT COUNT(*) FROM sys_role);
    RAISE NOTICE '- 菜单: % 个', (SELECT COUNT(*) FROM sys_menu);
    RAISE NOTICE '- 用户: % 个', (SELECT COUNT(*) FROM sys_user);
    RAISE NOTICE '========================================';
    RAISE NOTICE '默认账号信息：';
    RAISE NOTICE '用户名: admin';
    RAISE NOTICE '密码: admin123';
    RAISE NOTICE '========================================';
END $$;
