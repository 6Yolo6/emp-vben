package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysRoleTableDef extends TableDef {

    /**
     * 角色实体
 
 @author emp
     */
    public static final SysRoleTableDef SYS_ROLE = new SysRoleTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn ROLE_KEY = new QueryColumn(this, "role_key");

    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    public final QueryColumn ROLE_NAME = new QueryColumn(this, "role_name");

    public final QueryColumn ROLE_SORT = new QueryColumn(this, "role_sort");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    public final QueryColumn DATA_SCOPE = new QueryColumn(this, "data_scope");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn DEPT_CHECK_STRICTLY = new QueryColumn(this, "dept_check_strictly");

    public final QueryColumn MENU_CHECK_STRICTLY = new QueryColumn(this, "menu_check_strictly");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, REMARK, STATUS, ROLE_KEY, CREATE_BY, ROLE_NAME, ROLE_SORT, TENANT_ID, UPDATE_BY, DATA_SCOPE, CREATE_TIME, UPDATE_TIME, DEPT_CHECK_STRICTLY, MENU_CHECK_STRICTLY};

    public SysRoleTableDef() {
        super("", "sys_role");
    }

    private SysRoleTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysRoleTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysRoleTableDef("", "sys_role", alias));
    }

}
