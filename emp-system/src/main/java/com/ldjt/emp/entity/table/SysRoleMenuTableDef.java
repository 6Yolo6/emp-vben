package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysRoleMenuTableDef extends TableDef {

    /**
     * 角色菜单关联实体

 @author emp
     */
    public static final SysRoleMenuTableDef SYS_ROLE_MENU = new SysRoleMenuTableDef();

    public final QueryColumn MENU_ID = new QueryColumn(this, "menu_id");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{MENU_ID, ROLE_ID, TENANT_ID};

    public SysRoleMenuTableDef() {
        super("", "sys_role_menu");
    }

    private SysRoleMenuTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysRoleMenuTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysRoleMenuTableDef("", "sys_role_menu", alias));
    }

}
