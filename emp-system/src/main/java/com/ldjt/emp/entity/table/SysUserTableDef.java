package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysUserTableDef extends TableDef {

    /**
     * 用户实体类

 @author emp
     */
    public static final SysUserTableDef SYS_USER = new SysUserTableDef();

    /**
     * 用户ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 用户性别(0男 1女 2未知)
     */
    public final QueryColumn SEX = new QueryColumn(this, "sex");

    /**
     * 邮箱
     */
    public final QueryColumn EMAIL = new QueryColumn(this, "email");

    /**
     * 头像地址
     */
    public final QueryColumn AVATAR = new QueryColumn(this, "avatar");

    /**
     * 部门ID
     */
    public final QueryColumn DEPT_ID = new QueryColumn(this, "dept_id");

    /**
     * 手机号
     */
    public final QueryColumn MOBILE = new QueryColumn(this, "mobile");

    /**
     * 备注
     */
    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    /**
     * 状态(0停用 1正常)
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 删除标志(0未删除 1已删除)
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 最后登录IP
     */
    public final QueryColumn LOGIN_IP = new QueryColumn(this, "login_ip");

    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    /**
     * 昵称
     */
    public final QueryColumn NICKNAME = new QueryColumn(this, "nickname");

    /**
     * 密码
     */
    public final QueryColumn PASSWORD = new QueryColumn(this, "password");

    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    /**
     * 用户名
     */
    public final QueryColumn USERNAME = new QueryColumn(this, "username");

    /**
     * 最后登录时间
     */
    public final QueryColumn LOGIN_DATE = new QueryColumn(this, "login_date");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 主岗位ID
     */
    public final QueryColumn MAIN_POST_ID = new QueryColumn(this, "main_post_id");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 默认租户ID
     */
    public final QueryColumn DEFAULT_TENANT_ID = new QueryColumn(this, "default_tenant_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, SEX, EMAIL, AVATAR, DEPT_ID, MOBILE, REMARK, STATUS, DELETED, LOGIN_IP, CREATE_BY, NICKNAME, PASSWORD, UPDATE_BY, USERNAME, LOGIN_DATE, CREATE_TIME, MAIN_POST_ID, UPDATE_TIME, DEFAULT_TENANT_ID};

    public SysUserTableDef() {
        super("", "sys_user");
    }

    private SysUserTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysUserTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysUserTableDef("", "sys_user", alias));
    }

}
