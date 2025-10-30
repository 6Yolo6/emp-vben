package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysDeptTableDef extends TableDef {

    /**
     * 部门实体
 
 @author emp
     */
    public static final SysDeptTableDef SYS_DEPT = new SysDeptTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn EMAIL = new QueryColumn(this, "email");

    public final QueryColumn PHONE = new QueryColumn(this, "phone");

    public final QueryColumn LEADER = new QueryColumn(this, "leader");

    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    public final QueryColumn DEPT_NAME = new QueryColumn(this, "dept_name");

    public final QueryColumn ORDER_NUM = new QueryColumn(this, "order_num");

    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    public final QueryColumn ANCESTORS = new QueryColumn(this, "ancestors");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, EMAIL, PHONE, LEADER, REMARK, STATUS, CREATE_BY, DEPT_NAME, ORDER_NUM, PARENT_ID, TENANT_ID, UPDATE_BY, ANCESTORS, CREATE_TIME, UPDATE_TIME};

    public SysDeptTableDef() {
        super("", "sys_dept");
    }

    private SysDeptTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysDeptTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysDeptTableDef("", "sys_dept", alias));
    }

}
