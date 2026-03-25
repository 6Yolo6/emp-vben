-- ========================================
-- ERP Flowable 工作流相关表结构
-- ========================================

USE erp_framework;
SET NAMES utf8mb4;

-- ----------------------------
-- 任务超期配置表
-- ----------------------------
DROP TABLE IF EXISTS `flow_task_timeout_config`;
CREATE TABLE `flow_task_timeout_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_key` varchar(64) NOT NULL COMMENT '流程定义标识',
  `task_definition_key` varchar(64) DEFAULT NULL COMMENT '任务定义标识',
  `task_name` varchar(100) DEFAULT NULL COMMENT '任务名称',
  `config_name` varchar(100) NOT NULL COMMENT '配置名称',
  `timeout_minutes` int(11) NOT NULL COMMENT '超期时间（分钟）',
  `action_type` int(11) NOT NULL COMMENT '处理类型（1-提醒 2-转办 3-升级 4-自动完成）',
  `auto_transfer` tinyint(1) DEFAULT '0' COMMENT '是否自动转办',
  `transfer_type` int(11) DEFAULT NULL COMMENT '转办类型（1-指定用户 2-上级领导 3-角色用户 4-部门负责人）',
  `transfer_user_id` varchar(64) DEFAULT NULL COMMENT '转办目标用户ID',
  `transfer_role_id` varchar(64) DEFAULT NULL COMMENT '转办目标角色ID',
  `escalation_level` int(11) DEFAULT NULL COMMENT '升级层级',
  `notification_config` json DEFAULT NULL COMMENT '通知配置（JSON格式）',
  `email_notification` tinyint(1) DEFAULT '0' COMMENT '是否启用邮件通知',
  `penalty_score` decimal(10,2) DEFAULT NULL COMMENT '扣分值',
  `action_description` varchar(500) DEFAULT NULL COMMENT '处理描述',
  `priority` int(11) DEFAULT '0' COMMENT '优先级',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `condition` varchar(500) DEFAULT NULL COMMENT '执行条件（SpEL表达式）',
  `extend_properties` json DEFAULT NULL COMMENT '扩展属性（JSON格式）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_process_key` (`process_key`),
  KEY `idx_task_definition_key` (`task_definition_key`),
  KEY `idx_enabled_status` (`enabled`, `status`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务超期配置表';

-- ----------------------------
-- 任务超期记录表
-- ----------------------------
DROP TABLE IF EXISTS `flow_task_timeout_record`;
CREATE TABLE `flow_task_timeout_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_instance_id` varchar(64) NOT NULL COMMENT '流程实例ID',
  `task_id` varchar(64) NOT NULL COMMENT '任务ID',
  `task_name` varchar(100) DEFAULT NULL COMMENT '任务名称',
  `task_definition_key` varchar(64) DEFAULT NULL COMMENT '任务定义标识',
  `process_key` varchar(64) DEFAULT NULL COMMENT '流程定义标识',
  `business_key` varchar(64) DEFAULT NULL COMMENT '业务标识',
  `original_assignee` varchar(64) DEFAULT NULL COMMENT '原处理人ID',
  `original_assignee_name` varchar(50) DEFAULT NULL COMMENT '原处理人姓名',
  `task_create_time` datetime DEFAULT NULL COMMENT '任务创建时间',
  `timeout_time` datetime DEFAULT NULL COMMENT '超期时间',
  `actual_process_time` datetime DEFAULT NULL COMMENT '实际处理时间',
  `timeout_minutes` int(11) DEFAULT NULL COMMENT '超期分钟数',
  `config_id` bigint(20) DEFAULT NULL COMMENT '配置ID',
  `action_type` int(11) NOT NULL COMMENT '处理类型（1-提醒 2-转办 3-升级 4-自动完成）',
  `action_status` int(11) DEFAULT '0' COMMENT '处理状态（0-待处理 1-已处理 2-处理失败）',
  `transfer_user_id` varchar(64) DEFAULT NULL COMMENT '转办目标用户ID',
  `transfer_user_name` varchar(50) DEFAULT NULL COMMENT '转办目标用户姓名',
  `escalation_level` int(11) DEFAULT NULL COMMENT '升级层级',
  `action_result` varchar(500) DEFAULT NULL COMMENT '处理结果',
  `error_message` varchar(1000) DEFAULT NULL COMMENT '错误信息',
  `notification_status` int(11) DEFAULT '0' COMMENT '通知状态（0-未通知 1-已通知 2-通知失败）',
  `notification_time` datetime DEFAULT NULL COMMENT '通知时间',
  `penalty_score` decimal(10,2) DEFAULT NULL COMMENT '扣分值',
  `penalty_applied` tinyint(1) DEFAULT '0' COMMENT '是否已扣分',
  `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',
  `max_retry_count` int(11) DEFAULT '3' COMMENT '最大重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
  `extend_properties` json DEFAULT NULL COMMENT '扩展属性（JSON格式）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_process_instance_id` (`process_instance_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_process_key` (`process_key`),
  KEY `idx_business_key` (`business_key`),
  KEY `idx_original_assignee` (`original_assignee`),
  KEY `idx_timeout_time` (`timeout_time`),
  KEY `idx_action_status` (`action_status`),
  KEY `idx_notification_status` (`notification_status`),
  KEY `idx_config_id` (`config_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务超期记录表';
