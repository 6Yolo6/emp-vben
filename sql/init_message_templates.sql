-- =====================================================
-- 消息模板初始化脚本 - PostgreSQL
-- =====================================================
-- 这个脚本包含了常用的消息模板，支持变量替换
-- 表结构要求：sys_message_template
-- =====================================================

-- 1. 用户注册通知
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(1, 'USER_REGISTER', '用户注册通知', 1, '欢迎注册', '尊敬的 ${userName}，您已成功注册！登录地址：${loginUrl}', '[{"key":"userName","name":"用户名","type":"string","description":"注册用户的用户名"},{"key":"loginUrl","name":"登录地址","type":"string","description":"系统登录地址"}]', '用户注册成功通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- 2. 密码重置通知
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(2, 'PASSWORD_RESET', '密码重置通知', 1, '密码重置成功', '您的密码已重置，新密码为：${newPassword}。请妥善保管。如非本人操作，请立即联系管理员。链接有效期：${expiryTime}', '[{"key":"newPassword","name":"新密码","type":"string","description":"重置后的新密码"},{"key":"expiryTime","name":"过期时间","type":"string","description":"重置链接或新密码的过期时间"}]', '密码重置通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- 3. 任务分配通知
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(3, 'TASK_ASSIGN', '任务分配通知', 2, '新任务分配', '您有新的任务：${taskName}，任务描述：${taskDescription}。优先级：${priority}，截止时间：${deadline}。请及时处理。', '[{"key":"taskName","name":"任务名称","type":"string","description":"分配给您的任务名称"},{"key":"taskDescription","name":"任务描述","type":"string","description":"任务的具体描述"},{"key":"priority","name":"优先级","type":"string","description":"任务优先级（高/中/低）"},{"key":"deadline","name":"截止时间","type":"string","description":"任务完成的截止日期"}]', '任务分配通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- 4. 账户锁定通知
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(4, 'ACCOUNT_LOCK', '账户锁定通知', 1, '账户被锁定', '您的账户因${reason}已被锁定。如有疑问，请联系管理员：${contactAdmin}。', '[{"key":"reason","name":"锁定原因","type":"string","description":"账户被锁定的原因"},{"key":"contactAdmin","name":"联系管理员","type":"string","description":"管理员联系方式"}]', '账户锁定通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- 5. 订单通知
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(5, 'ORDER_NOTIFY', '订单通知', 2, '订单已创建', '您的订单已成功创建。订单号：${orderNo}，订单金额：${orderAmount}，下单时间：${orderTime}，预计送达时间：${deliveryTime}。感谢您的购买。', '[{"key":"orderNo","name":"订单号","type":"string","description":"订单编号"},{"key":"orderAmount","name":"订单金额","type":"string","description":"订单总金额（含货币符号）"},{"key":"orderTime","name":"下单时间","type":"string","description":"订单创建时间"},{"key":"deliveryTime","name":"预计送达","type":"string","description":"预计送达时间"}]', '订单通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- 6. 告警通知
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(6, 'ALERT_NOTIFY', '系统告警通知', 3, '系统告警', '[告警] ${alertType} - ${alertLevel}。告警时间：${alertTime}。告警内容：${alertContent}。请及时查看系统日志并处理。', '[{"key":"alertType","name":"告警类型","type":"string","description":"告警类型名称"},{"key":"alertLevel","name":"告警级别","type":"string","description":"严重程度（严重/重要/普通/提示）"},{"key":"alertTime","name":"告警时间","type":"string","description":"告警发生时间"},{"key":"alertContent","name":"告警内容","type":"string","description":"具体告警信息"}]', '系统告警通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- 7. 审批流程通知
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(7, 'APPROVAL_NOTIFY', '审批流程通知', 2, '需要您的审批', '您有一个待审批的流程：${processName}。申请人：${applicant}，申请时间：${applyTime}。请在${deadline}前进行审批。', '[{"key":"processName","name":"流程名称","type":"string","description":"审批流程的名称"},{"key":"applicant","name":"申请人","type":"string","description":"提交审批申请的人员名称"},{"key":"applyTime","name":"申请时间","type":"string","description":"提交申请的时间"},{"key":"deadline","name":"审批截止时间","type":"string","description":"必须完成审批的截止时间"}]', '审批流程通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- 8. 系统维护通知
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(8, 'SYSTEM_MAINTENANCE', '系统维护通知', 1, '系统将进行维护', '亲爱的用户，系统将于${maintenanceTime}进行定期维护，维护时间：${duration}。在此期间系统将无法使用，给您带来不便敬请谅解。${details}', '[{"key":"maintenanceTime","name":"维护时间","type":"string","description":"系统维护开始的具体时间"},{"key":"duration","name":"维护时长","type":"string","description":"预计维护需要的时长"},{"key":"details","name":"维护说明","type":"string","description":"关于维护的详细说明"}]', '系统维护通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- 9. 账户过期通知
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(9, 'ACCOUNT_EXPIRE', '账户即将过期通知', 1, '账户即将过期', '您的账户将于${expireDate}过期，在此之前请及时续费。有效期剩余${daysLeft}天。请访问${renewUrl}进行续费。', '[{"key":"expireDate","name":"过期日期","type":"string","description":"账户过期的具体日期"},{"key":"daysLeft","name":"剩余天数","type":"string","description":"账户有效期剩余的天数"},{"key":"renewUrl","name":"续费链接","type":"string","description":"账户续费的链接地址"}]', '账户过期通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- 10. 消息通知确认
INSERT INTO "public"."sys_message_template" 
("id", "template_code", "template_name", "template_type", "title_template", "content_template", "variables", "description", "status", "version", "tenant_id", "create_by", "create_time", "update_by", "update_time", "deleted") 
VALUES 
(10, 'MESSAGE_CONFIRM', '消息确认通知', 2, '请确认消息', '尊敬的${userName}，您收到来自${senderName}的消息。消息内容：${messageContent}。请在${confirmDeadline}前进行确认。', '[{"key":"userName","name":"收信人姓名","type":"string","description":"消息接收者的名称"},{"key":"senderName","name":"发送人姓名","type":"string","description":"消息发送者的名称"},{"key":"messageContent","name":"消息内容","type":"string","description":"需要确认的消息内容"},{"key":"confirmDeadline","name":"确认截止时间","type":"string","description":"需要确认消息的截止时间"}]', '消息确认通知', 1, 1, 1, NULL, now(), NULL, now(), 0);

-- =====================================================
-- 注意事项：
-- =====================================================
-- 1. id 字段需要根据实际数据库的自增序列进行调整
-- 2. tenant_id 默认为 1，请根据实际情况修改
-- 3. variables 字段采用 JSON 数组格式，包含：
--    - key: 变量键名（用于消息内容中的占位符）
--    - name: 显示名称（用户界面显示）
--    - type: 变量类型（通常为 string）
--    - description: 变量描述（用户界面帮助文本）
-- 4. title_template 和 content_template 中使用 ${key} 格式的占位符
-- 5. 所有模板状态默认为启用（status = 1）
-- 6. 模板类型对应关系：
--    1 = 系统通知
--    2 = 业务消息
--    3 = 预警消息
-- =====================================================
