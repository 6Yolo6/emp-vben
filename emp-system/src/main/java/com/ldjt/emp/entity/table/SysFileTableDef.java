package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 系统文件表定义
 *
 * @author system
 */
public class SysFileTableDef extends TableDef {

    public static final SysFileTableDef SYS_FILE = new SysFileTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn FILE_NAME = new QueryColumn(this, "file_name");
    public final QueryColumn ORIGINAL_NAME = new QueryColumn(this, "original_name");
    public final QueryColumn FILE_PATH = new QueryColumn(this, "file_path");
    public final QueryColumn FILE_URL = new QueryColumn(this, "file_url");
    public final QueryColumn FILE_SIZE = new QueryColumn(this, "file_size");
    public final QueryColumn FILE_TYPE = new QueryColumn(this, "file_type");
    public final QueryColumn FILE_EXT = new QueryColumn(this, "file_ext");
    public final QueryColumn STORAGE_TYPE = new QueryColumn(this, "storage_type");
    public final QueryColumn BUCKET_NAME = new QueryColumn(this, "bucket_name");
    public final QueryColumn OBJECT_KEY = new QueryColumn(this, "object_key");
    public final QueryColumn THUMBNAIL_URL = new QueryColumn(this, "thumbnail_url");
    public final QueryColumn MD5 = new QueryColumn(this, "md5");
    public final QueryColumn STATUS = new QueryColumn(this, "status");
    public final QueryColumn REMARK = new QueryColumn(this, "remark");
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    public SysFileTableDef() {
        super("", "sys_file");
    }
}
