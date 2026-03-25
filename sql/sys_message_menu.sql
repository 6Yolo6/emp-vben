-- 消息管理菜单SQL

-- 1. 消息管理菜单（父菜单）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
VALUES ('消息管理', (SELECT id FROM sys_menu WHERE menu_name = '系统管理' AND parent_id = 0 LIMIT 1), 9, 'message', NULL, 'M', 1, 1, NULL, 'mail', 1, NOW(), NOW(), 0);

-- 获取消息管理菜单ID（用于后续插入）
DO $$
DECLARE
    message_menu_id BIGINT;
    message_list_menu_id BIGINT;
    message_template_menu_id BIGINT;
    admin_role_id BIGINT;
    menu_record RECORD;
BEGIN
    SELECT id INTO message_menu_id FROM sys_menu WHERE menu_name = '消息管理' AND parent_id != 0 ORDER BY id DESC LIMIT 1;

    -- 1.1 消息列表菜单
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES ('消息列表', message_menu_id, 1, 'list', 'system/message/index', 'C', 1, 1, 'system:message:list', 'mail', 1, NOW(), NOW(), 0);

    -- 获取消息列表菜单ID
    SELECT id INTO message_list_menu_id FROM sys_menu WHERE menu_name = '消息列表' AND parent_id = message_menu_id LIMIT 1;

    -- 消息列表按钮权限
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES 
    ('消息查询', message_list_menu_id, 1, '', NULL, 'F', 1, 1, 'system:message:query', '#', 1, NOW(), NOW(), 0),
    ('消息发送', message_list_menu_id, 2, '', NULL, 'F', 1, 1, 'system:message:send', '#', 1, NOW(), NOW(), 0),
    ('消息详情', message_list_menu_id, 3, '', NULL, 'F', 1, 1, 'system:message:detail', '#', 1, NOW(), NOW(), 0),
    ('消息删除', message_list_menu_id, 4, '', NULL, 'F', 1, 1, 'system:message:remove', '#', 1, NOW(), NOW(), 0),
    ('标记已读', message_list_menu_id, 5, '', NULL, 'F', 1, 1, 'system:message:read', '#', 1, NOW(), NOW(), 0);

    -- 1.2 消息模板菜单
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES ('消息模板', message_menu_id, 2, 'template', 'system/message/template/index', 'C', 1, 1, 'system:template:list', 'file-text', 1, NOW(), NOW(), 0);

    -- 获取消息模板菜单ID
    SELECT id INTO message_template_menu_id FROM sys_menu WHERE menu_name = '消息模板' AND parent_id = message_menu_id LIMIT 1;

    -- 消息模板按钮权限
    INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, update_time, deleted)
    VALUES 
    ('模板查询', message_template_menu_id, 1, '', NULL, 'F', 1, 1, 'system:template:query', '#', 1, NOW(), NOW(), 0),
    ('模板新增', message_template_menu_id, 2, '', NULL, 'F', 1, 1, 'system:template:add', '#', 1, NOW(), NOW(), 0),
    ('模板修改', message_template_menu_id, 3, '', NULL, 'F', 1, 1, 'system:template:edit', '#', 1, NOW(), NOW(), 0),
    ('模板删除', message_template_menu_id, 4, '', NULL, 'F', 1, 1, 'system:template:remove', '#', 1, NOW(), NOW(), 0);

    -- 为超级管理员角色分配新菜单权限
    SELECT id INTO admin_role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1;

    -- 为新增的菜单分配权限
    FOR menu_record IN 
        SELECT id FROM sys_menu 
        WHERE menu_name IN ('消息管理', '消息列表', '消息模板')
           OR perms IN ('system:message:query', 'system:message:send', 'system:message:detail', 'system:message:remove', 'system:message:read',
                        'system:template:query', 'system:template:add', 'system:template:edit', 'system:template:remove')
    LOOP
        INSERT INTO sys_role_menu (role_id, menu_id)
        VALUES (admin_role_id, menu_record.id)
        ON CONFLICT DO NOTHING;
    END LOOP;
END $$;
