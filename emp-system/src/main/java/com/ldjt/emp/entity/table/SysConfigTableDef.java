package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 系统参数配置表定义
 *
 * @author system
 */
public class SysConfigTableDef extends TableDef {

    public static final SysConfigTableDef SYS_CONFIG = new SysConfigTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn CONFIG_NAME = new QueryColumn(this, "config_name");
    public final QueryColumn CONFIG_KEY = new QueryColumn(this, "config_key");
    public final QueryColumn CONFIG_VALUE = new QueryColumn(this, "config_value");
    public final QueryColumn CONFIG_TYPE = new QueryColumn(this, "config_type");
    public final QueryColumn REMARK = new QueryColumn(this, "remark");
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    public SysConfigTableDef() {
        super("", "sys_config");
    }
}
