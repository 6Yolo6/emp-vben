package com.ldjt.emp.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class SysMenuTableDef extends TableDef {

    /**
     * 菜单实体

 @author emp
     */
    public static final SysMenuTableDef SYS_MENU = new SysMenuTableDef();

    /**
     * 菜单ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 菜单图标
     */
    public final QueryColumn ICON = new QueryColumn(this, "icon");

    /**
     * 路由地址
     */
    public final QueryColumn PATH = new QueryColumn(this, "path");

    /**
     * 权限标识
     */
    public final QueryColumn PERMS = new QueryColumn(this, "perms");

    /**
     * 状态(0停用 1正常)
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 删除标志(0未删除 1已删除)
     */
    public final QueryColumn DELETED = new QueryColumn(this, "deleted");

    /**
     * 显示状态(0隐藏 1显示)
     */
    public final QueryColumn VISIBLE = new QueryColumn(this, "visible");

    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    /**
     * 菜单名称
     */
    public final QueryColumn MENU_NAME = new QueryColumn(this, "menu_name");

    /**
     * 菜单类型(M目录 C菜单 F按钮)
     */
    public final QueryColumn MENU_TYPE = new QueryColumn(this, "menu_type");

    /**
     * 显示顺序
     */
    public final QueryColumn ORDER_NUM = new QueryColumn(this, "order_num");

    /**
     * 父菜单ID
     */
    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");

    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    public final QueryColumn TENANT_ID = new QueryColumn(this, "tenant_id");

    /**
     * 组件路径
     */
    public final QueryColumn COMPONENT = new QueryColumn(this, "component");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ICON, PATH, PERMS, STATUS, DELETED, VISIBLE, CREATE_BY, MENU_NAME, MENU_TYPE, ORDER_NUM, PARENT_ID, UPDATE_BY, COMPONENT, CREATE_TIME, UPDATE_TIME};

    public SysMenuTableDef() {
        super("", "sys_menu");
    }

    private SysMenuTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public SysMenuTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysMenuTableDef("", "sys_menu", alias));
    }

}
