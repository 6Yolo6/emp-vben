package com.ldjt.emp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ldjt.emp.common.core.domain.Result;
import com.ldjt.emp.common.exception.BusinessException;
import com.ldjt.emp.dto.tenant.TenantCreateDTO;
import com.ldjt.emp.dto.tenant.TenantQueryDTO;
import com.ldjt.emp.dto.tenant.TenantUpdateDTO;
import com.ldjt.emp.entity.SysTenant;
import com.ldjt.emp.service.SysTenantService;
import com.ldjt.emp.vo.tenant.TenantVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysTenantTableDef.SYS_TENANT;

/**
 * 租户管理控制器
 * 
 * @author EMP Team
 * @since 2025-10-22
 */
@Tag(name = "租户管理")
@Slf4j
@RestController
@RequestMapping("/system/tenant")
@RequiredArgsConstructor
public class SysTenantController {
    
    private final SysTenantService tenantService;
    
    @Operation(summary = "创建租户")
    @PostMapping
    @SaCheckPermission("system:tenant:add")
    public Result<Void> create(@Valid @RequestBody TenantCreateDTO dto) {
        if (!tenantService.checkTenantCodeUnique(dto.getTenantCode(), null)) {
            throw new BusinessException("租户编码已存在");
        }
        
        SysTenant tenant = new SysTenant();
        BeanUtils.copyProperties(dto, tenant);
        tenantService.save(tenant);
        
        log.info("创建租户成功: {}", dto.getTenantCode());
        return Result.success();
    }
    
    @Operation(summary = "更新租户")
    @PutMapping("/{id}")
    @SaCheckPermission("system:tenant:edit")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TenantUpdateDTO dto) {
        SysTenant existTenant = tenantService.getById(id);
        if (existTenant == null) {
            throw new BusinessException("租户不存在");
        }
        
        if (!tenantService.checkTenantCodeUnique(dto.getTenantCode(), id)) {
            throw new BusinessException("租户编码已存在");
        }
        
        SysTenant tenant = new SysTenant();
        BeanUtils.copyProperties(dto, tenant);
        tenant.setId(id);
        tenantService.updateById(tenant);
        
        log.info("更新租户成功: {}", dto.getTenantCode());
        return Result.success();
    }
    
    @Operation(summary = "删除租户")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:tenant:remove")
    public Result<Void> delete(@PathVariable Long id) {
        tenantService.removeById(id);
        log.info("删除租户成功: id={}", id);
        return Result.success();
    }
    
    @Operation(summary = "根据ID查询租户")
    @GetMapping("/{id}")
    @SaCheckPermission("system:tenant:query")
    public Result<TenantVO> getById(@PathVariable Long id) {
        SysTenant tenant = tenantService.getById(id);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        
        TenantVO vo = new TenantVO();
        BeanUtils.copyProperties(tenant, vo);
        return Result.success(vo);
    }
    
    @Operation(summary = "查询所有租户")
    @GetMapping("/list")
    @SaCheckPermission("system:tenant:query")
    public Result<List<TenantVO>> list() {
        List<SysTenant> tenants = tenantService.list();
        List<TenantVO> vos = tenants.stream().map(tenant -> {
            TenantVO vo = new TenantVO();
            BeanUtils.copyProperties(tenant, vo);
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(vos);
    }
    
    @Operation(summary = "分页查询租户")
    @GetMapping("/page")
    @SaCheckPermission("system:tenant:query")
    public Result<Page<TenantVO>> page(TenantQueryDTO dto) {
        QueryWrapper query = QueryWrapper.create()
                .where(SYS_TENANT.DELETED.eq(0))
                .and(SYS_TENANT.TENANT_CODE.like(dto.getTenantCode(), dto.getTenantCode() != null))
                .and(SYS_TENANT.TENANT_NAME.like(dto.getTenantName(), dto.getTenantName() != null))
                .and(SYS_TENANT.TENANT_TYPE.eq(dto.getTenantType(), dto.getTenantType() != null))
                .and(SYS_TENANT.STATUS.eq(dto.getStatus(), dto.getStatus() != null))
                .orderBy(SYS_TENANT.CREATE_TIME.desc());
        
        Page<SysTenant> page = tenantService.page(
                new Page<>(dto.getPageNum(), dto.getPageSize()),
                query
        );
        
        Page<TenantVO> voPage = new Page<>();
        voPage.setPageNumber(page.getPageNumber());
        voPage.setPageSize(page.getPageSize());
        voPage.setTotalRow(page.getTotalRow());
        voPage.setRecords(page.getRecords().stream().map(tenant -> {
            TenantVO vo = new TenantVO();
            BeanUtils.copyProperties(tenant, vo);
            return vo;
        }).collect(Collectors.toList()));
        
        return Result.success(voPage);
    }
    
    @Operation(summary = "修改租户状态")
    @PutMapping("/{id}/status")
    @SaCheckPermission("system:tenant:edit")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysTenant tenant = new SysTenant();
        tenant.setId(id);
        tenant.setStatus(status);
        tenantService.updateById(tenant);
        
        log.info("修改租户状态成功: id={}, status={}", id, status);
        return Result.success();
    }
}
