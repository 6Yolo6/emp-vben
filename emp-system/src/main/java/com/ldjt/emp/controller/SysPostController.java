package com.ldjt.emp.controller;

import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.entity.SysPost;
import com.ldjt.emp.service.SysPostService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理控制器
 * 
 * @author emp
 */
@Tag(name = "岗位管理")
@Slf4j
@RestController
@RequestMapping("/system/post")
public class SysPostController {
    
    @Autowired
    private SysPostService postService;
    
    /**
     * 创建岗位
     */
    @Operation(summary = "创建岗位")
    @PostMapping
    public Result<Void> create(@RequestBody SysPost post) {
        postService.createPost(post);
        return Result.success();
    }
    
    /**
     * 更新岗位
     */
    @Operation(summary = "更新岗位")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysPost post) {
        post.setId(id);
        postService.updatePost(post);
        return Result.success();
    }
    
    /**
     * 删除岗位
     */
    @Operation(summary = "删除岗位")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.success();
    }
    
    /**
     * 根据ID查询岗位
     */
    @Operation(summary = "根据ID查询岗位")
    @GetMapping("/{id}")
    public Result<SysPost> getById(@PathVariable Long id) {
        return Result.success(postService.getPostById(id));
    }
    
    /**
     * 查询所有岗位
     */
    @Operation(summary = "查询所有岗位")
    @GetMapping("/list")
    public Result<List<SysPost>> list() {
        return Result.success(postService.listAllPosts());
    }
    
    /**
     * 分页查询岗位
     */
    @Operation(summary = "分页查询岗位")
    @GetMapping("/page")
    public Result<Page<SysPost>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String postName,
            @RequestParam(required = false) Integer status) {
        return Result.success(postService.pageQuery(pageNum, pageSize, postName, status));
    }
    
    /**
     * 批量删除岗位
     */
    @Operation(summary = "批量删除岗位")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> postIds) {
        postService.batchDelete(postIds);
        return Result.success();
    }
    
    /**
     * 分配岗位角色
     */
    @Operation(summary = "分配岗位角色")
    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        postService.assignRoles(id, roleIds);
        return Result.success();
    }
    
    /**
     * 获取岗位角色
     */
    @Operation(summary = "获取岗位角色")
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getPostRoles(@PathVariable Long id) {
        return Result.success(postService.getPostRoleIds(id));
    }
    
    /**
     * 根据租户ID查询岗位列表
     */
    @Operation(summary = "根据租户ID查询岗位列表")
    @GetMapping("/list/by-tenant")
    public Result<List<SysPost>> listByTenant(@RequestParam Long tenantId) {
        return Result.success(postService.listPostsByTenant(tenantId));
    }
}
