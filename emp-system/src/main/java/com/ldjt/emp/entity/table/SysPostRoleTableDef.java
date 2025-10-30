package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysPostRoleTableDef extends TableDef {

    /**
     * 岗位角色关联实体

 @author emp
     */
    public static final SysPostRoleTableDef SYS_POST_ROLE = new SysPostRoleTableDef();

    public final QueryColumn POST_ID = new QueryColumn(this, "post_id");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{POST_ID, ROLE_ID, TENANT_ID};

    public SysPostRoleTableDef() {
        super("", "sys_post_role");
    }

    private SysPostRoleTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysPostRoleTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysPostRoleTableDef("", "sys_post_role", alias));
    }

}
