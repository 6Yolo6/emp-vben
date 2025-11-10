package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 字典类型表定义
 */
public class SysDictTypeTableDef extends TableDef {

    public static final SysDictTypeTableDef SYS_DICT_TYPE = new SysDictTypeTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn DICT_NAME = new QueryColumn(this, "dict_name");
    public final QueryColumn DICT_TYPE = new QueryColumn(this, "dict_type");
    public final QueryColumn STATUS = new QueryColumn(this, "status");
    public final QueryColumn REMARK = new QueryColumn(this, "remark");
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public SysDictTypeTableDef() {
        super("", "sys_dict_type");
    }
}
