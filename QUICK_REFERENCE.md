# 消息模板功能快速参考

## 📋 核心概念

| 概念 | 说明 | 示例 |
|-----|------|------|
| **模板编码** | 唯一标识符 | `PASSWORD_RESET` |
| **变量键名** | 在内容中使用的占位符 | `${newPassword}` |
| **变量映射** | 键名到显示名称的映射 | `newPassword → 新密码` |
| **变量替换** | 将变量值填充到内容中 | `${newPassword}` → `Abc@1234` |

---

## 🔧 开发快速开始

### 后端：添加新模板类型

1. **在数据库中插入模板**

```sql
INSERT INTO sys_message_template 
(template_code, template_name, template_type, title_template, content_template, variables, status)
VALUES 
('NEW_TEMPLATE', '新模板', 1, '标题', '内容 ${var1} ${var2}',
'[{"key":"var1","name":"变量1",...},...]', 1);
```

2. **更新 API 映射**（如果需要）

```typescript
// src/api/sys/message-template.ts
'NEW_TEMPLATE': [
  { key: 'var1', name: '变量1', type: 'string', description: '描述' },
  { key: 'var2', name: '变量2', type: 'string', description: '描述' }
]
```

### 前端：使用模板发送消息

```vue
<!-- 1. 选择模板 -->
<Select v-model="selectedTemplateId" :options="templateOptions" />

<!-- 2. 自动填充标题和内容 -->
<!-- 3. 显示变量面板 -->
<div v-if="showVariablesPanel">
  <input v-for="variable in templateVariables" 
         v-model="variablesValues[variable.key]" />
</div>

<!-- 4. 点击应用变量 -->
<Button @click="applyVariablesToContent">应用变量</Button>

<!-- 5. 发送消息 -->
<Button @click="sendMessage">发送</Button>
```

---

## 📝 内置模板列表

### 系统通知类 (templateType=1)

| 模板编码 | 模板名称 | 变量 |
|---------|---------|------|
| `USER_REGISTER` | 用户注册通知 | userName, loginUrl |
| `PASSWORD_RESET` | 密码重置通知 | newPassword, expiryTime |
| `ACCOUNT_LOCK` | 账户锁定通知 | reason, contactAdmin |
| `ACCOUNT_EXPIRE` | 账户过期通知 | expireDate, daysLeft, renewUrl |
| `SYSTEM_MAINTENANCE` | 系统维护通知 | maintenanceTime, duration, details |

### 业务消息类 (templateType=2)

| 模板编码 | 模板名称 | 变量 |
|---------|---------|------|
| `TASK_ASSIGN` | 任务分配通知 | taskName, taskDescription, priority, deadline |
| `ORDER_NOTIFY` | 订单通知 | orderNo, orderAmount, orderTime, deliveryTime |
| `APPROVAL_NOTIFY` | 审批流程通知 | processName, applicant, applyTime, deadline |
| `MESSAGE_CONFIRM` | 消息确认通知 | userName, senderName, messageContent, confirmDeadline |

### 告警消息类 (templateType=3)

| 模板编码 | 模板名称 | 变量 |
|---------|---------|------|
| `ALERT_NOTIFY` | 系统告警通知 | alertType, alertLevel, alertTime, alertContent |

---

## 🔍 常用 SQL 查询

### 查询启用的模板
```sql
SELECT id, template_code, template_name, variables 
FROM sys_message_template 
WHERE status = 1 AND deleted = 0;
```

### 查询特定模板
```sql
SELECT * FROM sys_message_template 
WHERE template_code = 'PASSWORD_RESET' 
AND tenant_id = 1 AND deleted = 0;
```

### 更新模板变量
```sql
UPDATE sys_message_template 
SET variables = '[...]', update_time = now()
WHERE id = 1;
```

### 禁用模板
```sql
UPDATE sys_message_template 
SET status = 0, update_time = now()
WHERE template_code = 'OLD_TEMPLATE';
```

---

## 🎯 常见变量替换场景

### 场景 1：密码重置

**模板编码**：`PASSWORD_RESET`  
**模板内容**：
```
您的密码已重置，新密码为：${newPassword}
链接有效期：${expiryTime}
```

**填充变量**：
```json
{
  "newPassword": "TempPass123!",
  "expiryTime": "24小时"
}
```

**最终内容**：
```
您的密码已重置，新密码为：TempPass123!
链接有效期：24小时
```

### 场景 2：任务分配

**模板编码**：`TASK_ASSIGN`  
**模板内容**：
```
您有新的任务：${taskName}
描述：${taskDescription}
优先级：${priority}
截止时间：${deadline}
```

**填充变量**：
```json
{
  "taskName": "完成需求评审",
  "taskDescription": "评审新功能需求文档",
  "priority": "高",
  "deadline": "2025-01-20"
}
```

### 场景 3：订单通知

**模板编码**：`ORDER_NOTIFY`  
**模板内容**：
```
订单号：${orderNo}
金额：${orderAmount}
下单时间：${orderTime}
预计送达：${deliveryTime}
```

---

## 🐛 调试技巧

### 前端调试

```typescript
// 1. 检查模板加载
console.log('模板列表:', templateOptions.value);

// 2. 检查变量解析
console.log('解析的变量:', templateVariables.value);

// 3. 检查变量值
console.log('变量值:', variablesValues.value);

// 4. 检查替换结果
const newContent = replaceVariables(content, variablesValues.value);
console.log('替换后内容:', newContent);

// 5. 检查表单值
const values = await formApi.getValues();
console.log('表单值:', values);
```

### 后端调试

```java
// 1. 检查模板查询
SysMessageTemplate template = templateService.getTemplateByCode("PASSWORD_RESET");
log.info("模板: {}", template);

// 2. 检查变量替换
String content = "密码：${password}";
String result = replaceVariables(content, Map.of("password", "Abc123"));
log.info("替换结果: {}", result);

// 3. 检查消息发送
log.info("发送消息，接收人类型: {}, 接收人: {}", receiverType, receiverIds);
```

---

## ⚙️ 配置检查清单

- [ ] 数据库中模板表存在
- [ ] 初始化 SQL 已执行
- [ ] 后端 Controller 已部署
- [ ] API 路由正确
- [ ] 前端 API 请求地址正确
- [ ] TypeScript 类型定义完整
- [ ] 变量映射已更新
- [ ] 模板编码与后端一致

---

## 📞 获取帮助

### 问题排查步骤

1. **检查模板是否存在**
   ```sql
   SELECT * FROM sys_message_template WHERE template_code = 'YOUR_CODE';
   ```

2. **检查变量是否正确**
   ```typescript
   const vars = parseTemplateVariables('YOUR_CODE', template.variables);
   console.log(vars); // 应该返回变量数组
   ```

3. **检查变量替换是否工作**
   ```typescript
   const result = replaceVariables('${key}', { key: 'value' });
   console.assert(result === 'value');
   ```

4. **检查消息是否发送**
   ```sql
   SELECT * FROM sys_message WHERE receiver_type = 1 ORDER BY create_time DESC LIMIT 1;
   ```

---

## 📚 相关文件

| 文件 | 说明 |
|-----|------|
| `sql/init_message_templates.sql` | 模板初始化脚本 |
| `apps/web-antd/src/api/sys/message-template.ts` | API 定义 |
| `apps/web-antd/src/views/system/message/components/MessageSendModal.vue` | 发送组件 |
| `emp-system/src/main/java/.../SysMessageTemplateService.java` | 后端服务 |
| `MESSAGE_TEMPLATE_INTEGRATION_GUIDE.md` | 完整集成指南 |
| `MESSAGE-TEMPLATE-GUIDE.md` | 前端使用指南 |

---

**快速链接**：
- 🔗 [完整集成指南](MESSAGE_TEMPLATE_INTEGRATION_GUIDE.md)
- 🔗 [前端使用指南](apps/web-antd/src/views/system/message/MESSAGE-TEMPLATE-GUIDE.md)
- 🔗 [初始化脚本](sql/init_message_templates.sql)

**最后更新**：2025-01-18
