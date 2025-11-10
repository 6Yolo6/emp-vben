-- 用户状态字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_time, update_time)
VALUES ('用户状态', 'sys_user_status', 1, '用户在职状态字典', NOW(), NOW());

-- 用户状态字典数据
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, dict_sort, list_class, is_default, status, remark, create_time, update_time)
VALUES 
('sys_user_status', '在职', '1', 1, 'success', 1, 1, '用户在职状态', NOW(), NOW()),
('sys_user_status', '辞职', '2', 2, 'error', 0, 1, '用户辞职状态', NOW(), NOW()),
('sys_user_status', '调出', '3', 3, 'warning', 0, 1, '用户调出状态', NOW(), NOW()),
('sys_user_status', '退休', '4', 4, 'default', 0, 1, '用户退休状态', NOW(), NOW());
