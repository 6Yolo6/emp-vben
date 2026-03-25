package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysMessageTemplateTableDef extends TableDef {

    /**
     * 消息模板实体
     */
    public static final SysMessageTemplateTableDef SYS_MESSAGE_TEMPLATE = new SysMessageTemplateTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn VERSION = new QueryColumn(this, "version");

    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    public final QueryColumn VARIABLES = new QueryColumn(this, "variables");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    public final QueryColumn TEMPLATE_CODE = new QueryColumn(this, "template_code");

    public final QueryColumn TEMPLATE_NAME = new QueryColumn(this, "template_name");

    public final QueryColumn TEMPLATE_TYPE = new QueryColumn(this, "template_type");

    public final QueryColumn TITLE_TEMPLATE = new QueryColumn(this, "title_template");

    public final QueryColumn CONTENT_TEMPLATE = new QueryColumn(this, "content_template");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, STATUS, VERSION, CREATE_BY, TENANT_ID, UPDATE_BY, VARIABLES, CREATE_TIME, UPDATE_TIME, DESCRIPTION, TEMPLATE_CODE, TEMPLATE_NAME, TEMPLATE_TYPE, TITLE_TEMPLATE, CONTENT_TEMPLATE};

    public SysMessageTemplateTableDef() {
        super("", "sys_message_template");
    }

    private SysMessageTemplateTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysMessageTemplateTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysMessageTemplateTableDef("", "sys_message_template", alias));
    }

}
