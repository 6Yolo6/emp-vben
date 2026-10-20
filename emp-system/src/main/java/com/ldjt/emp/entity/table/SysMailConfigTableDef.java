package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 邮件配置表定义
 *
 * @author kiro
 */
public class SysMailConfigTableDef extends TableDef {

    public static final SysMailConfigTableDef SYS_MAIL_CONFIG = new SysMailConfigTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn CONFIG_NAME = new QueryColumn(this, "config_name");
    public final QueryColumn SMTP_HOST = new QueryColumn(this, "smtp_host");
    public final QueryColumn SMTP_PORT = new QueryColumn(this, "smtp_port");
    public final QueryColumn USERNAME = new QueryColumn(this, "username");
    public final QueryColumn PASSWORD = new QueryColumn(this, "password");
    public final QueryColumn FROM_NAME = new QueryColumn(this, "from_name");
    public final QueryColumn SSL_ENABLE = new QueryColumn(this, "ssl_enable");
    public final QueryColumn IS_DEFAULT = new QueryColumn(this, "is_default");
    public final QueryColumn STATUS = new QueryColumn(this, "status");
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public SysMailConfigTableDef() {
        super("", "sys_mail_config");
    }

    public SysMailConfigTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysMailConfigTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysMailConfigTableDef("", "sys_mail_config", alias));
    }
}
