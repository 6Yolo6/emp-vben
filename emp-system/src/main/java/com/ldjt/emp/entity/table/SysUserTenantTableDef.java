package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysUserTenantTableDef extends TableDef {

    /**
     * 用户租户关联实体
 
 @author EMP Team
 @since 2025-10-22
     */
    public static final SysUserTenantTableDef SYS_USER_TENANT = new SysUserTenantTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 部门ID（该用户在此租户下的部门）
     */
    public final QueryColumn DEPT_ID = new QueryColumn(this, "dept_id");

    /**
     * 状态(0停用 1正常)
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 用户ID
     */
    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 用户加入该单位的时间
     */
    public final QueryColumn JOIN_TIME = new QueryColumn(this, "join_time");

    /**
     * 租户ID
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 是否为用户的主要单位
     */
    public final QueryColumn IS_PRIMARY = new QueryColumn(this, "is_primary");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, DEPT_ID, STATUS, USER_ID, JOIN_TIME, TENANT_ID, IS_PRIMARY};

    public SysUserTenantTableDef() {
        super("", "sys_user_tenant");
    }

    private SysUserTenantTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysUserTenantTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysUserTenantTableDef("", "sys_user_tenant", alias));
    }

}
