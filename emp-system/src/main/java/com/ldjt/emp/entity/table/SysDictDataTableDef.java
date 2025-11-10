package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 字典数据表定义
 */
public class SysDictDataTableDef extends TableDef {

    public static final SysDictDataTableDef SYS_DICT_DATA = new SysDictDataTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn DICT_SORT = new QueryColumn(this, "dict_sort");
    public final QueryColumn DICT_LABEL = new QueryColumn(this, "dict_label");
    public final QueryColumn DICT_VALUE = new QueryColumn(this, "dict_value");
    public final QueryColumn DICT_TYPE = new QueryColumn(this, "dict_type");
    public final QueryColumn CSS_CLASS = new QueryColumn(this, "css_class");
    public final QueryColumn LIST_CLASS = new QueryColumn(this, "list_class");
    public final QueryColumn IS_DEFAULT = new QueryColumn(this, "is_default");
    public final QueryColumn STATUS = new QueryColumn(this, "status");
    public final QueryColumn REMARK = new QueryColumn(this, "remark");
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public SysDictDataTableDef() {
        super("", "sys_dict_data");
    }
}
