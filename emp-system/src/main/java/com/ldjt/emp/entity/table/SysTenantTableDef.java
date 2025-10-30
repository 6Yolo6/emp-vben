package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 租户表 TableDef
 * 
 * @author EMP Team
 */
public class SysTenantTableDef extends TableDef {

    public static final SysTenantTableDef SYS_TENANT = new SysTenantTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn TENANT_CODE = new QueryColumn(this, "tenant_code");
    public final QueryColumn TENANT_NAME = new QueryColumn(this, "tenant_name");
    public final QueryColumn TENANT_TYPE = new QueryColumn(this, "tenant_type");
    public final QueryColumn CONTACT_NAME = new QueryColumn(this, "contact_name");
    public final QueryColumn CONTACT_PHONE = new QueryColumn(this, "contact_phone");
    public final QueryColumn CONTACT_EMAIL = new QueryColumn(this, "contact_email");
    public final QueryColumn DOMAIN = new QueryColumn(this, "domain");
    public final QueryColumn LOGO = new QueryColumn(this, "logo");
    public final QueryColumn EXPIRE_TIME = new QueryColumn(this, "expire_time");
    public final QueryColumn ACCOUNT_LIMIT = new QueryColumn(this, "account_limit");
    public final QueryColumn STORAGE_LIMIT = new QueryColumn(this, "storage_limit");
    public final QueryColumn STATUS = new QueryColumn(this, "status");
    public final QueryColumn REMARK = new QueryColumn(this, "remark");
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{
        ID, TENANT_CODE, TENANT_NAME, TENANT_TYPE, CONTACT_NAME, CONTACT_PHONE,
        CONTACT_EMAIL, DOMAIN, LOGO, EXPIRE_TIME, ACCOUNT_LIMIT, STORAGE_LIMIT,
        STATUS, REMARK, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME
    };

    public SysTenantTableDef() {
        super("", "sys_tenant");
    }
    
    private SysTenantTableDef(String schema, String name, String alias) {
        super(schema, name, alias);
    }
    
    public SysTenantTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysTenantTableDef("", "sys_tenant", alias));
    }
}
