package com.ldjt.emp.mapper;

import com.ldjt.emp.entity.SysUser;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
