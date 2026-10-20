package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 邮件模板表定义
 *
 * @author kiro
 */
public class SysMailTemplateTableDef extends TableDef {

    public static final SysMailTemplateTableDef SYS_MAIL_TEMPLATE = new SysMailTemplateTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn TEMPLATE_CODE = new QueryColumn(this, "template_code");
    public final QueryColumn TEMPLATE_NAME = new QueryColumn(this, "template_name");
    public final QueryColumn SUBJECT = new QueryColumn(this, "subject");
    public final QueryColumn CONTENT = new QueryColumn(this, "content");
    public final QueryColumn TEMPLATE_TYPE = new QueryColumn(this, "template_type");
    public final QueryColumn VARIABLES = new QueryColumn(this, "variables");
    public final QueryColumn STATUS = new QueryColumn(this, "status");
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public SysMailTemplateTableDef() {
        super("", "sys_mail_template");
    }

    public SysMailTemplateTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysMailTemplateTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysMailTemplateTableDef("", "sys_mail_template", alias));
    }
}
