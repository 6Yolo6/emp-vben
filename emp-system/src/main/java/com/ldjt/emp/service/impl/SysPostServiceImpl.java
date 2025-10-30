package com.ldjt.emp.service.impl;

import com.ldjt.emp.entity.SysPost;
import com.ldjt.emp.entity.SysPostRole;
import com.ldjt.emp.framework.tenant.TenantContextHolder;
import com.ldjt.emp.mapper.SysPostMapper;
import com.ldjt.emp.mapper.SysPostRoleMapper;
import com.ldjt.emp.mapper.SysUserPostMapper;
import com.ldjt.emp.service.PermissionService;
import com.ldjt.emp.service.SysPostService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysPostTableDef.SYS_POST;
import static com.ldjt.emp.entity.table.SysPostRoleTableDef.SYS_POST_ROLE;
import static com.ldjt.emp.entity.table.SysUserPostTableDef.SYS_USER_POST;

/**
 * 岗位服务实现
 *
 * @author emp
 */
@Slf4j
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {

    @Autowired
    private SysPostRoleMapper sysPostRoleMapper;

    @Autowired
    private SysUserPostMapper sysUserPostMapper;

    @Autowired
    private PermissionService permissionService;

    @Override
    public boolean createPost(SysPost post) {
        // 检查岗位编码唯一性
        SysPost existing = getByPostCode(post.getPostCode());
        if (existing != null) {
            throw new RuntimeException("岗位编码已存在");
        }
        return save(post);
    }

    @Override
    public boolean updatePost(SysPost post) {
        // 检查岗位编码唯一性（排除自己）
        SysPost existing = getByPostCode(post.getPostCode());
        if (existing != null && !existing.getId().equals(post.getId())) {
            throw new RuntimeException("岗位编码已存在");
        }
        return updateById(post);
    }

    @Override
    public boolean deletePost(Long id) {
        // 检查是否有用户关联此岗位
        QueryWrapper userQuery = QueryWrapper.create()
                .where(SYS_USER_POST.POST_ID.eq(id));
        long userCount = sysUserPostMapper.selectCountByQuery(userQuery);

        if (userCount > 0) {
            throw new RuntimeException("该岗位下还有" + userCount + "个用户，无法删除");
        }

        // 检查是否有角色关联
        QueryWrapper roleQuery = QueryWrapper.create()
                .where(SYS_POST_ROLE.POST_ID.eq(id));
        long roleCount = sysPostRoleMapper.selectCountByQuery(roleQuery);

        if (roleCount > 0) {
            throw new RuntimeException("该岗位还关联了" + roleCount + "个角色，无法删除");
        }

        return removeById(id);
    }

    @Override
    public boolean hasUsers(Long postId) {
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_USER_POST.POST_ID.eq(postId));
        long count = sysUserPostMapper.selectCountByQuery(query);
        return count > 0;
    }

    @Override
    public boolean batchDelete(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return false;
        }

        // 逐个检查并删除
        for (Long postId : postIds) {
            deletePost(postId);
        }

        return true;
    }

    @Override
    public SysPost getPostById(Long id) {
        return getById(id);
    }

    @Override
    public List<SysPost> listAllPosts() {
        Long tenantId = TenantContextHolder.getTenantId();
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_POST.TENANT_ID.eq(tenantId))
                .and(SYS_POST.STATUS.eq(1))
                .orderBy(SYS_POST.POST_SORT.asc());

        // 岗位表没有dept_id字段，不应用数据权限过滤
        // 岗位是租户级别的资源，只需要租户过滤即可

        return list(query);
    }

    @Override
    public Page<SysPost> pageQuery(int pageNum, int pageSize, String postName, Integer status) {
        Long tenantId = TenantContextHolder.getTenantId();
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_POST.TENANT_ID.eq(tenantId))
                .and(SYS_POST.POST_NAME.like(postName, StringUtils.hasText(postName)))
                .and(SYS_POST.STATUS.eq(status, status != null))
                .orderBy(SYS_POST.POST_SORT.asc());

        // 岗位表没有dept_id字段，不应用数据权限过滤
        // 岗位是租户级别的资源，只需要租户过滤即可

        return page(new Page<>(pageNum, pageSize), query);
    }

    @Override
    public SysPost getByPostCode(String postCode) {
        return getOne(QueryWrapper.create()
                .where(SYS_POST.POST_CODE.eq(postCode)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long postId, List<Long> roleIds) {
        Long tenantId = TenantContextHolder.getTenantId();

        // 先删除岗位原有的角色关联（添加租户过滤）
        QueryWrapper deleteWrapper = QueryWrapper.create()
                .where(SYS_POST_ROLE.POST_ID.eq(postId))
                .and(SYS_POST_ROLE.TENANT_ID.eq(tenantId));
        sysPostRoleMapper.deleteByQuery(deleteWrapper);

        // 批量插入新的角色关联（添加租户ID）
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysPostRole postRole = new SysPostRole();
                postRole.setPostId(postId);
                postRole.setRoleId(roleId);
                postRole.setTenantId(tenantId); // 添加租户ID
                sysPostRoleMapper.insert(postRole);
            }
        }

        // TODO: 清除相关用户的权限缓存

        return true;
    }

    @Override
    public List<Long> getPostRoleIds(Long postId) {
        Long tenantId = TenantContextHolder.getTenantId();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_POST_ROLE.POST_ID.eq(postId))
                .and(SYS_POST_ROLE.TENANT_ID.eq(tenantId));
        List<SysPostRole> postRoles = sysPostRoleMapper.selectListByQuery(queryWrapper);
        return postRoles.stream()
                .map(SysPostRole::getRoleId)
                .collect(Collectors.toList());
    }


    @Override
    public List<SysPost> listPostsByTenant(Long tenantId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_POST.TENANT_ID.eq(tenantId))
                .and(SYS_POST.DELETED.eq(0))
                .orderBy(SYS_POST.POST_SORT.asc());
        return list(queryWrapper);
    }
}
