package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysUserPostTableDef extends TableDef {

    /**
     * 用户岗位关联实体
 
 @author emp
     */
    public static final SysUserPostTableDef SYS_USER_POST = new SysUserPostTableDef();

    public final QueryColumn POST_ID = new QueryColumn(this, "post_id");

    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 租户ID（该岗位关联在哪个租户下）
     */
    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{POST_ID, USER_ID, TENANT_ID};

    public SysUserPostTableDef() {
        super("", "sys_user_post");
    }

    private SysUserPostTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysUserPostTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysUserPostTableDef("", "sys_user_post", alias));
    }

}
