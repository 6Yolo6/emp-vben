package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysRoleDeptTableDef extends TableDef {

    /**
     * 角色部门关联实体
 用于自定义数据权限

 @author emp
     */
    public static final SysRoleDeptTableDef SYS_ROLE_DEPT = new SysRoleDeptTableDef();

    /**
     * 部门ID
     */
    public final QueryColumn DEPT_ID = new QueryColumn(this, "dept_id");

    /**
     * 角色ID
     */
    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

    /**
     * 租户ID（该岗位关联在哪个租户下）
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{DEPT_ID, ROLE_ID, TENANT_ID};

    public SysRoleDeptTableDef() {
        super("", "sys_role_dept");
    }

    private SysRoleDeptTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysRoleDeptTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysRoleDeptTableDef("", "sys_role_dept", alias));
    }

}
