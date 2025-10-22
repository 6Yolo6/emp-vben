package com.ldjt.emp.mapper;

import com.ldjt.emp.entity.SysUser;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author emp
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 更新用户登录信息
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @param loginDate 登录时间
     */
    void updateLoginInfo(@Param("userId") Long userId, 
                        @Param("loginIp") String loginIp, 
                        @Param("loginDate") java.time.LocalDateTime loginDate);
    
    /**
     * 查询用户的岗位ID列表
     */
    @Select("SELECT post_id FROM sys_user_post WHERE user_id = #{userId}")
    List<Long> selectUserPostIds(@Param("userId") Long userId);
    
    /**
     * 查询岗位关联的角色ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT role_id FROM sys_post_role WHERE post_id IN " +
            "<foreach collection='postIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Long> selectPostRoleIds(@Param("postIds") List<Long> postIds);
    
    /**
     * 查询用户直接分配的角色ID列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectDirectUserRoleIds(@Param("userId") Long userId);
    
    /**
     * 根据角色ID列表查询菜单权限标识
     */
    @Select("<script>" +
            "SELECT DISTINCT m.perms FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id IN " +
            "<foreach collection='roleIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND m.status = 1 AND m.deleted = 0 AND m.perms IS NOT NULL AND m.perms != ''" +
            "</script>")
    List<String> selectMenuPermissionsByRoleIds(@Param("roleIds") List<Long> roleIds);
    
    /**
     * 根据岗位ID列表查询角色标识
     */
    @Select("<script>" +
            "SELECT DISTINCT r.role_key FROM sys_role r " +
            "INNER JOIN sys_post_role pr ON r.id = pr.role_id " +
            "WHERE pr.post_id IN " +
            "<foreach collection='postIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND r.status = 1 AND r.deleted = 0" +
            "</script>")
    List<String> selectRoleCodesByPostIds(@Param("postIds") List<Long> postIds);
    
    /**
     * 根据用户ID查询直接分配的角色标识
     */
    @Select("SELECT DISTINCT r.role_key FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 AND r.deleted = 0")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
