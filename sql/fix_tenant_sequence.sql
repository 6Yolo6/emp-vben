-- 修复租户表的主键序列问题
-- 当手动插入数据或导入数据后，序列可能不同步

-- 查看当前最大 ID
SELECT MAX(id) as max_id FROM sys_tenant;

-- 重置 sys_tenant 表的序列（使用 true 参数立即生效）
SELECT setval('sys_tenant_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_tenant), true);

-- 测试序列是否正常（获取下一个值）
SELECT nextval('sys_tenant_id_seq') as next_id;
