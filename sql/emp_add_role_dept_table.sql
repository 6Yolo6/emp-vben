-- ========================================
-- 添加角色自定义部门关联表
-- 用于自定义数据权限功能
-- ========================================

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- ========================================
-- 角色部门关联表 (sys_role_dept)
-- ========================================
DROP TABLE IF EXISTS sys_role_dept CASCADE;
CREATE TABLE sys_role_dept (
    role_id         BIGINT          NOT NULL,
    dept_id         BIGINT          NOT NULL,
    PRIMARY KEY (role_id, dept_id)
);

COMMENT ON TABLE sys_role_dept IS '角色部门关联表(用于自定义数据权限)';
COMMENT ON COLUMN sys_role_dept.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_dept.dept_id IS '部门ID';

CREATE INDEX idx_role_dept_role_id ON sys_role_dept(role_id);
CREATE INDEX idx_role_dept_dept_id ON sys_role_dept(dept_id);

-- ========================================
-- 完成提示
-- ========================================
DO $$
BEGIN
    RAISE NOTICE '角色部门关联表创建完成！';
    RAISE NOTICE '该表用于存储角色的自定义数据权限部门';
END $$;
