package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 邮件发送记录表定义
 *
 * @author kiro
 */
public class SysMailLogTableDef extends TableDef {

    public static final SysMailLogTableDef SYS_MAIL_LOG = new SysMailLogTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn CONFIG_ID = new QueryColumn(this, "config_id");
    public final QueryColumn TEMPLATE_ID = new QueryColumn(this, "template_id");
    public final QueryColumn TO_EMAILS = new QueryColumn(this, "to_emails");
    public final QueryColumn CC_EMAILS = new QueryColumn(this, "cc_emails");
    public final QueryColumn BCC_EMAILS = new QueryColumn(this, "bcc_emails");
    public final QueryColumn SUBJECT = new QueryColumn(this, "subject");
    public final QueryColumn CONTENT = new QueryColumn(this, "content");
    public final QueryColumn ATTACHMENTS = new QueryColumn(this, "attachments");
    public final QueryColumn SEND_STATUS = new QueryColumn(this, "send_status");
    public final QueryColumn ERROR_MESSAGE = new QueryColumn(this, "error_message");
    public final QueryColumn RETRY_COUNT = new QueryColumn(this, "retry_count");
    public final QueryColumn SEND_TIME = new QueryColumn(this, "send_time");
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public SysMailLogTableDef() {
        super("", "sys_mail_log");
    }

    public SysMailLogTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysMailLogTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysMailLogTableDef("", "sys_mail_log", alias));
    }
}
