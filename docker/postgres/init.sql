-- EMP Platform 数据库初始化脚本
-- 创建时间: 2025-01-10

-- 设置时区
SET timezone = 'Asia/Shanghai';

-- 创建数据库（如果不存在）
-- 注意：在docker-entrypoint-initdb.d中执行时，数据库已经由环境变量创建

-- 连接到emp_dev数据库
\c emp_dev;

-- 创建扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 创建序列生成器函数（用于雪花算法ID生成，这里简化使用序列）
CREATE SEQUENCE IF NOT EXISTS global_id_seq START WITH 1 INCREMENT BY 1;

-- ========================================
-- 执行核心表结构和数据初始化
-- ========================================

\echo '========================================';
\echo '开始创建核心数据库表...';
\echo '========================================';

-- 创建核心表结构
\i /docker-entrypoint-initdb.d/emp_core_tables.sql

\echo '========================================';
\echo '开始初始化核心数据...';
\echo '========================================';

-- 初始化核心数据
\i /docker-entrypoint-initdb.d/emp_core_data.sql

-- 初始化完成提示
DO $$
BEGIN
    RAISE NOTICE '===========================================';
    RAISE NOTICE 'EMP Platform 数据库初始化完成';
    RAISE NOTICE '数据库名称: emp_dev';
    RAISE NOTICE '字符集: UTF8';
    RAISE NOTICE '时区: Asia/Shanghai';
    RAISE NOTICE '===========================================';
END $$;
