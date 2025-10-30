package com.ldjt.emp.converter;

import com.ldjt.emp.entity.*;
import com.ldjt.emp.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户转换器
 * 用于 Entity 和 VO 之间的转换
 *
 * @author EMP Team
 */
public class UserConverter {

    /**
     * 转换单个用户为 VO
     */
    public static UserVO toVO(SysUser user) {
        if (user == null) {
            return null;
        }

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 设置性别名称
        vo.setSexName(getSexName(user.getSex()));

        // 设置状态名称
        vo.setStatusName(getStatusName(user.getStatus()));

        return vo;
    }

    /**
     * 转换用户为 VO，包含关联信息（简化版本，不包含主岗位和租户）
     */
    public static UserVO toVO(SysUser user, SysDept dept,
                              List<SysPost> posts, List<SysRole> roles) {
        return toVO(user, dept, posts, roles, null, null);
    }

    /**
     * 转换用户为 VO，包含关联信息（完整版本）
     */
    public static UserVO toVO(SysUser user, SysDept dept,
                              List<SysPost> posts, List<SysRole> roles, SysPost mainPost, SysTenant tenant) {
        UserVO vo = toVO(user);
        if (vo == null) {
            return null;
        }

        // 设置部门信息
        if (dept != null) {
            vo.setDeptName(dept.getDeptName());
        }

        // 设置岗位信息
        if (posts != null && !posts.isEmpty()) {
            vo.setPostIds(posts.stream().map(SysPost::getId).collect(Collectors.toList()));
            vo.setPostNames(posts.stream().map(SysPost::getPostName).collect(Collectors.toList()));
        }

        // 设置主岗位信息
        if (mainPost != null) {
            vo.setMainPostName(mainPost.getPostName());
        }

        // 设置角色信息
        if (roles != null && !roles.isEmpty()) {
            vo.setRoleIds(roles.stream().map(SysRole::getId).collect(Collectors.toList()));
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        }

        // 设置租户信息
        if (tenant != null) {
            vo.setTenantName(tenant.getTenantName());
        }

        return vo;
    }

    /**
     * 批量转换用户为 VO（简化版本，不包含主岗位和租户）
     * 用于列表查询，使用 Map 来避免 N+1 查询
     */
    public static UserVO toVO(SysUser user,
                              Map<Long, SysDept> deptMap,
                              Map<Long, List<SysPost>> userPostsMap,
                              Map<Long, List<SysRole>> userRolesMap) {
        return toVO(user, deptMap, userPostsMap, userRolesMap, null, null);
    }

    /**
     * 批量转换用户为 VO（完整版本）
     * 用于列表查询，使用 Map 来避免 N+1 查询
     */
    public static UserVO toVO(SysUser user,
                              Map<Long, SysDept> deptMap,
                              Map<Long, List<SysPost>> userPostsMap,
                              Map<Long, List<SysRole>> userRolesMap,
                              Map<Long, SysPost> mainPostMap,
                              Map<Long, SysTenant> userTenantMap) {
        UserVO vo = toVO(user);
        if (vo == null) {
            return null;
        }

        // 设置部门信息
        if (deptMap != null && user.getDeptId() != null) {
            SysDept dept = deptMap.get(user.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }

        // 设置岗位信息
        if (userPostsMap != null) {
            List<SysPost> posts = userPostsMap.get(user.getId());
            if (posts != null && !posts.isEmpty()) {
                vo.setPostIds(posts.stream().map(SysPost::getId).collect(Collectors.toList()));
                vo.setPostNames(posts.stream().map(SysPost::getPostName).collect(Collectors.toList()));
            }
        }

        // 设置主岗位信息
        if (mainPostMap != null && user.getMainPostId() != null) {
            SysPost mainPost = mainPostMap.get(user.getMainPostId());
            if (mainPost != null) {
                vo.setMainPostName(mainPost.getPostName());
            }
        }

        // 设置角色信息
        if (userRolesMap != null) {
            List<SysRole> roles = userRolesMap.get(user.getId());
            if (roles != null && !roles.isEmpty()) {
                vo.setRoleIds(roles.stream().map(SysRole::getId).collect(Collectors.toList()));
                vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
            }
        }

        // 设置租户信息（通过用户ID从userTenantMap获取）
        if (userTenantMap != null) {
            SysTenant tenant = userTenantMap.get(user.getId());
            if (tenant != null) {
                vo.setTenantName(tenant.getTenantName());
                vo.setTenantCode(tenant.getTenantCode());
            }
        }

        return vo;
    }

    /**
     * 获取性别名称
     */
    private static String getSexName(Integer sex) {
        if (sex == null) {
            return "未知";
        }
        switch (sex) {
            case 0:
                return "男";
            case 1:
                return "女";
            default:
                return "未知";
        }
    }

    /**
     * 获取状态名称
     */
    private static String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "正常" : "停用";
    }
    
    /**
     * 填充主单位信息到 UserVO
     * 用于列表查询时显示主单位的组织信息
     * 
     * @param vo 用户VO
     * @param primaryTenant 主租户配置
     * @param tenantMap 租户Map
     * @param deptMap 部门Map
     * @param postMap 岗位Map
     * @param userPostsMap 用户岗位Map（key: userId_tenantId）
     */
    public static void fillPrimaryTenantInfo(UserVO vo, 
                                            SysUserTenant primaryTenant,
                                            Map<Long, SysTenant> tenantMap,
                                            Map<Long, SysDept> deptMap,
                                            Map<Long, SysPost> postMap,
                                            Map<String, List<SysPost>> userPostsMap) {
        if (vo == null || primaryTenant == null) {
            return;
        }
        
        // 填充主单位名称
        if (tenantMap != null && primaryTenant.getTenantId() != null) {
            SysTenant tenant = tenantMap.get(primaryTenant.getTenantId());
            if (tenant != null) {
                vo.setPrimaryTenantName(tenant.getTenantName());
            }
        }
        
        // 填充主单位部门名称
        if (deptMap != null && primaryTenant.getDeptId() != null) {
            SysDept dept = deptMap.get(primaryTenant.getDeptId());
            if (dept != null) {
                vo.setPrimaryDeptName(dept.getDeptName());
            }
        }
        
        // 填充主单位岗位信息
        if (userPostsMap != null && postMap != null) {
            String key = vo.getId() + "_" + primaryTenant.getTenantId();
            List<SysPost> posts = userPostsMap.get(key);
            if (posts != null && !posts.isEmpty()) {
                vo.setPrimaryPostNames(posts.stream()
                    .map(SysPost::getPostName)
                    .collect(Collectors.toList()));
            }
        }
        
        // 填充主单位主岗位名称
        if (postMap != null && primaryTenant.getMainPostId() != null) {
            SysPost mainPost = postMap.get(primaryTenant.getMainPostId());
            if (mainPost != null) {
                vo.setPrimaryMainPostName(mainPost.getPostName());
            }
        }
        
        // 填充主单位状态
        vo.setPrimaryStatus(primaryTenant.getStatus());
        vo.setPrimaryStatusName(getTenantStatusName(primaryTenant.getStatus()));
    }
    
    /**
     * 获取租户状态名称
     */
    private static String getTenantStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "在职";
            case 2:
                return "辞职";
            case 3:
                return "调出";
            case 4:
                return "退休";
            default:
                return "未知";
        }
    }
}
