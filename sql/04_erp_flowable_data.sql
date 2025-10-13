-- ========================================
-- ERP Flowable 工作流初始化数据
-- ========================================

USE erp_framework;
SET NAMES utf8mb4;

-- ----------------------------
-- 初始化超期配置数据
-- ----------------------------
INSERT INTO `flow_task_timeout_config` (`process_key`, `task_definition_key`, `task_name`, `config_name`, `timeout_minutes`, `action_type`, `auto_transfer`, `transfer_type`, `transfer_user_id`, `priority`, `enabled`, `action_description`, `creator`) VALUES
('leave_process', 'dept_leader_approve', '部门领导审批', '请假申请部门领导审批超期配置', 1440, 2, 1, 2, NULL, 1, 1, '部门领导审批超过24小时自动转办给上级领导', '1'),
('leave_process', 'hr_approve', 'HR审批', '请假申请HR审批超期配置', 2880, 3, 1, 3, 'hr_role', 2, 1, 'HR审批超过48小时自动升级处理', '1'),
('expense_process', 'finance_approve', '财务审批', '报销申请财务审批超期配置', 4320, 1, 0, NULL, NULL, 1, 1, '财务审批超过72小时发送提醒', '1');