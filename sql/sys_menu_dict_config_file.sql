-- 字典管理、参数配置、文件管理菜单SQL

-- 1. 字典管理菜单（父菜单）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
VALUES ('字典管理', (SELECT id FROM sys_menu WHERE menu_name = '系统管理' AND parent_id = 0 LIMIT 1), 6, 'dict', NULL, 'M', 1, 1, NULL, 'book-open', 1, NOW(), NOW(), 0);

-- 获取字典管理菜单ID（用于后续插入）
DO $$
DECLARE
    dict_menu_id BIGINT;
    dict_type_menu_id BIGINT;
    system_menu_id BIGINT;
    config_menu_id BIGINT;
    file_menu_id BIGINT;
    admin_role_id BIGINT;
    menu_record RECORD;
BEGIN
    SELECT id INTO dict_menu_id FROM sys_menu WHERE menu_name = '字典管理' AND parent_id != 0 ORDER BY id DESC LIMIT 1;

    -- 1.1 字典类型菜单
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES ('字典类型', dict_menu_id, 1, 'type', 'system/dict/type/index', 'C', 1, 1, 'system:dict:list', 'book-open', 1, NOW(), NOW(), 0);

    -- 获取字典类型菜单ID
    SELECT id INTO dict_type_menu_id FROM sys_menu WHERE menu_name = '字典类型' AND parent_id = dict_menu_id LIMIT 1;

    -- 字典类型按钮权限
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES 
    ('字典查询', dict_type_menu_id, 1, '', NULL, 'F', 1, 1, 'system:dict:query', '#', 1, NOW(), NOW(), 0),
    ('字典新增', dict_type_menu_id, 2, '', NULL, 'F', 1, 1, 'system:dict:add', '#', 1, NOW(), NOW(), 0),
    ('字典修改', dict_type_menu_id, 3, '', NULL, 'F', 1, 1, 'system:dict:edit', '#', 1, NOW(), NOW(), 0),
    ('字典删除', dict_type_menu_id, 4, '', NULL, 'F', 1, 1, 'system:dict:remove', '#', 1, NOW(), NOW(), 0),
    ('字典导出', dict_type_menu_id, 5, '', NULL, 'F', 1, 1, 'system:dict:export', '#', 1, NOW(), NOW(), 0);

    -- 1.2 字典数据菜单（隐藏）
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES ('字典数据', dict_menu_id, 2, 'data', 'system/dict/data/index', 'C', 0, 1, 'system:dict:list', 'book', 1, NOW(), NOW(), 0);

    -- 2. 参数配置菜单
    SELECT id INTO system_menu_id FROM sys_menu WHERE menu_name = '系统管理' AND parent_id = 0 LIMIT 1;

    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES ('参数配置', system_menu_id, 7, 'config', 'system/config/index', 'C', 1, 1, 'system:config:list', 'settings-2', 1, NOW(), NOW(), 0);

    SELECT id INTO config_menu_id FROM sys_menu WHERE menu_name = '参数配置' AND parent_id = system_menu_id LIMIT 1;

    -- 参数配置按钮权限
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES 
    ('参数查询', config_menu_id, 1, '', NULL, 'F', 1, 1, 'system:config:query', '#', 1, NOW(), NOW(), 0),
    ('参数新增', config_menu_id, 2, '', NULL, 'F', 1, 1, 'system:config:add', '#', 1, NOW(), NOW(), 0),
    ('参数修改', config_menu_id, 3, '', NULL, 'F', 1, 1, 'system:config:edit', '#', 1, NOW(), NOW(), 0),
    ('参数删除', config_menu_id, 4, '', NULL, 'F', 1, 1, 'system:config:remove', '#', 1, NOW(), NOW(), 0),
    ('参数导出', config_menu_id, 5, '', NULL, 'F', 1, 1, 'system:config:export', '#', 1, NOW(), NOW(), 0);

    -- 3. 文件管理菜单
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES ('文件管理', system_menu_id, 8, 'file', 'system/file/index', 'C', 1, 1, 'system:file:list', 'file', 1, NOW(), NOW(), 0);

    SELECT id INTO file_menu_id FROM sys_menu WHERE menu_name = '文件管理' AND parent_id = system_menu_id LIMIT 1;

    -- 文件管理按钮权限
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES 
    ('文件查询', file_menu_id, 1, '', NULL, 'F', 1, 1, 'system:file:query', '#', 1, NOW(), NOW(), 0),
    ('文件上传', file_menu_id, 2, '', NULL, 'F', 1, 1, 'system:file:upload', '#', 1, NOW(), NOW(), 0),
    ('文件下载', file_menu_id, 3, '', NULL, 'F', 1, 1, 'system:file:download', '#', 1, NOW(), NOW(), 0),
    ('文件删除', file_menu_id, 4, '', NULL, 'F', 1, 1, 'system:file:remove', '#', 1, NOW(), NOW(), 0);

    -- 为超级管理员角色分配新菜单权限
    SELECT id INTO admin_role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1;

    -- 为新增的菜单分配权限
    FOR menu_record IN 
        SELECT id FROM sys_menu 
        WHERE menu_name IN ('字典管理', '字典类型', '字典数据', '参数配置', '文件管理')
           OR perms IN ('system:dict:query', 'system:dict:add', 'system:dict:edit', 'system:dict:remove', 'system:dict:export',
                        'system:config:query', 'system:config:add', 'system:config:edit', 'system:config:remove', 'system:config:export',
                        'system:file:query', 'system:file:upload', 'system:file:download', 'system:file:remove')
    LOOP
        INSERT INTO sys_role_menu (role_id, menu_id)
        VALUES (admin_role_id, menu_record.id)
        ON CONFLICT DO NOTHING;
    END LOOP;
END $$;
