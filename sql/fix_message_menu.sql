-- 修复消息模块菜单显示问题

-- 1. 确保消息菜单的 component 路径正确
UPDATE sys_menu 
SET component = 'system/message/index'
WHERE menu_name = '消息列表';

UPDATE sys_menu 
SET component = 'system/message/template/index'
WHERE menu_name = '消息模板';

-- 2. 确保菜单状态正确（visible=1, status=1）
UPDATE sys_menu 
SET visible = 1, status = 1
WHERE menu_name IN ('消息管理', '消息列表', '消息模板');

-- 3. 检查并修复父菜单ID
DO $$
DECLARE
    system_menu_id BIGINT;
    message_parent_id BIGINT;
BEGIN
    -- 获取系统管理菜单ID
    SELECT id INTO system_menu_id 
    FROM sys_menu 
    WHERE menu_name = '系统管理' AND parent_id = 0 
    LIMIT 1;
    
    -- 更新消息管理的父菜单
    UPDATE sys_menu 
    SET parent_id = system_menu_id
    WHERE menu_name = '消息管理' AND menu_type = 'M';
    
    -- 获取消息管理菜单ID
    SELECT id INTO message_parent_id
    FROM sys_menu
    WHERE menu_name = '消息管理' AND menu_type = 'M'
    LIMIT 1;
    
    -- 更新子菜单的父ID
    UPDATE sys_menu
    SET parent_id = message_parent_id
    WHERE menu_name IN ('消息列表', '消息模板')
      AND menu_type = 'C';
END $$;

-- 4. 为超级管理员角色分配菜单权限
DO $$
DECLARE
    admin_role_id BIGINT;
    menu_record RECORD;
BEGIN
    -- 获取admin角色ID
    SELECT id INTO admin_role_id 
    FROM sys_role 
    WHERE role_key = 'admin' 
    LIMIT 1;
    
    -- 为所有消息相关菜单分配权限
    FOR menu_record IN 
        SELECT id FROM sys_menu 
        WHERE menu_name LIKE '%消息%' 
           OR perms LIKE '%message%' 
           OR perms LIKE '%template%'
    LOOP
        INSERT INTO sys_role_menu (role_id, menu_id, tenant_id)
        SELECT admin_role_id, menu_record.id, tenant_id
        FROM sys_role
        WHERE id = admin_role_id
        ON CONFLICT DO NOTHING;
    END LOOP;
END $$;

-- 5. 验证结果
SELECT 
    m.id,
    m.menu_name,
    m.parent_id,
    m.path,
    m.component,
    m.menu_type,
    m.visible,
    m.status,
    m.perms,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM sys_role_menu rm 
            JOIN sys_role r ON rm.role_id = r.id 
            WHERE rm.menu_id = m.id AND r.role_key = 'admin'
        ) THEN '已分配'
        ELSE '未分配'
    END as admin_permission
FROM sys_menu m
WHERE m.menu_name LIKE '%消息%' 
   OR m.perms LIKE '%message%' 
   OR m.perms LIKE '%template%'
ORDER BY m.id;
