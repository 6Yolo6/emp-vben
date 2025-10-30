package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysPostTableDef extends TableDef {

    /**
     * 岗位实体
 
 @author emp
     */
    public static final SysPostTableDef SYS_POST = new SysPostTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    public final QueryColumn POST_CODE = new QueryColumn(this, "post_code");

    public final QueryColumn POST_NAME = new QueryColumn(this, "post_name");

    public final QueryColumn POST_SORT = new QueryColumn(this, "post_sort");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, REMARK, STATUS, CREATE_BY, POST_CODE, POST_NAME, POST_SORT, TENANT_ID, UPDATE_BY, CREATE_TIME, UPDATE_TIME};

    public SysPostTableDef() {
        super("", "sys_post");
    }

    private SysPostTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysPostTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysPostTableDef("", "sys_post", alias));
    }

}
