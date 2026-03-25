package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysUserMessageTableDef extends TableDef {

    /**
     * 用户消息关联实体
     */
    public static final SysUserMessageTableDef SYS_USER_MESSAGE = new SysUserMessageTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn IS_READ = new QueryColumn(this, "is_read");

    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn READ_TIME = new QueryColumn(this, "read_time");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    public final QueryColumn MESSAGE_ID = new QueryColumn(this, "message_id");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, IS_READ, USER_ID, DELETED, READ_TIME, TENANT_ID, MESSAGE_ID, CREATE_TIME};

    public SysUserMessageTableDef() {
        super("", "sys_user_message");
    }

    private SysUserMessageTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysUserMessageTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysUserMessageTableDef("", "sys_user_message", alias));
    }

}
