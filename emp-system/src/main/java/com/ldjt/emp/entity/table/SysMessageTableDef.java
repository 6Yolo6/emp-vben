package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysMessageTableDef extends TableDef {

    /**
     * 站内消息实体
     */
    public static final SysMessageTableDef SYS_MESSAGE = new SysMessageTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn TITLE = new QueryColumn(this, "title");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn CONTENT = new QueryColumn(this, "content");

    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    public final QueryColumn SENDER_ID = new QueryColumn(this, "sender_id");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn SENDER_NAME = new QueryColumn(this, "sender_name");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn MESSAGE_TYPE = new QueryColumn(this, "message_type");

    public final QueryColumn RECEIVER_IDS = new QueryColumn(this, "receiver_ids");

    public final QueryColumn RECEIVER_TYPE = new QueryColumn(this, "receiver_type");

    public final QueryColumn RECEIVER_ROLE_IDS = new QueryColumn(this, "receiver_role_ids");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, TITLE, STATUS, CONTENT, CREATE_BY, SENDER_ID, TENANT_ID, UPDATE_BY, CREATE_TIME, SENDER_NAME, UPDATE_TIME, MESSAGE_TYPE, RECEIVER_IDS, RECEIVER_TYPE, RECEIVER_ROLE_IDS};

    public SysMessageTableDef() {
        super("", "sys_message");
    }

    private SysMessageTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysMessageTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysMessageTableDef("", "sys_message", alias));
    }

}
