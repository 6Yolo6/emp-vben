package com.ldjt.emp.service;

import com.ldjt.emp.entity.SysPost;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 岗位服务接口
 * 
 * @author emp
 */
public interface SysPostService extends IService<SysPost> {
    
    /**
     * 创建岗位
     */
    boolean createPost(SysPost post);
    
    /**
     * 更新岗位
     */
    boolean updatePost(SysPost post);
    
    /**
     * 删除岗位（检查关联）
     */
    boolean deletePost(Long id);
    
    /**
     * 根据ID查询岗位
     */
    SysPost getPostById(Long id);
    
    /**
     * 查询所有岗位
     */
    List<SysPost> listAllPosts();
    
    /**
     * 分页查询岗位
     */
    Page<SysPost> pageQuery(int pageNum, int pageSize, String postName, Integer status);
    
    /**
     * 根据岗位编码查询
     */
    SysPost getByPostCode(String postCode);
    
    /**
     * 检查岗位是否有关联用户
     */
    boolean hasUsers(Long postId);
    
    /**
     * 批量删除岗位
     */
    boolean batchDelete(List<Long> postIds);
    
    /**
     * 分配岗位角色
     *
     * @param postId 岗位ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRoles(Long postId, List<Long> roleIds);
    
    /**
     * 获取岗位的角色ID列表
     *
     * @param postId 岗位ID
     * @return 角色ID列表
     */
    List<Long> getPostRoleIds(Long postId);
    
    /**
     * 根据租户ID查询岗位列表
     *
     * @param tenantId 租户ID
     * @return 岗位列表
     */
    List<SysPost> listPostsByTenant(Long tenantId);
}
