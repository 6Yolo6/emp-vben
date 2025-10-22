package com.ldjt.emp.service.impl;

import com.ldjt.emp.dto.DeptTreeDTO;
import com.ldjt.emp.entity.SysDept;
import com.ldjt.emp.mapper.SysDeptMapper;
import com.ldjt.emp.service.SysDeptService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ldjt.emp.entity.table.SysDeptTableDef.SYS_DEPT;

/**
 * 部门服务实现类
 *
 * @author emp
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public List<DeptTreeDTO> getDeptTree() {
        // 查询所有部门
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_DEPT.DELETED.eq(0))
                .orderBy(SYS_DEPT.ORDER_NUM.asc());
        List<SysDept> allDepts = this.list(queryWrapper);
        
        // 构建树形结构，从根节点（parentId = 0）开始
        return buildDeptTree(allDepts, 0L);
    }
    
    @Override
    public List<DeptTreeDTO> buildDeptTree(List<SysDept> depts, Long parentId) {
        List<DeptTreeDTO> tree = new ArrayList<>();
        
        for (SysDept dept : depts) {
            if (dept.getParentId() != null && dept.getParentId().equals(parentId)) {
                DeptTreeDTO node = new DeptTreeDTO();
                BeanUtils.copyProperties(dept, node);
                
                // 递归查找子部门
                List<DeptTreeDTO> children = buildDeptTree(depts, dept.getId());
                node.setChildren(children);
                
                tree.add(node);
            }
        }
        
        return tree;
    }
    
    @Override
    public boolean createDept(SysDept dept) {
        // 设置ancestors字段
        if (dept.getParentId() != null && dept.getParentId() != 0) {
            SysDept parent = getById(dept.getParentId());
            if (parent != null) {
                dept.setAncestors(parent.getAncestors() + "," + dept.getParentId());
            }
        } else {
            dept.setAncestors("0");
        }
        return save(dept);
    }
    
    @Override
    public boolean updateDept(SysDept dept) {
        return updateById(dept);
    }
    
    @Override
    public boolean deleteDept(Long deptId) {
        // 检查是否有子部门
        if (hasChildren(deptId)) {
            throw new RuntimeException("该部门下存在子部门，无法删除");
        }
        
        // 检查是否有用户
        if (hasUsers(deptId)) {
            throw new RuntimeException("该部门下存在用户，无法删除");
        }
        
        return removeById(deptId);
    }
    
    @Override
    public boolean hasChildren(Long deptId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_DEPT.PARENT_ID.eq(deptId))
                .and(SYS_DEPT.DELETED.eq(0));
        return count(queryWrapper) > 0;
    }
    
    @Override
    public boolean hasUsers(Long deptId) {
        // 查询sys_user表检查是否有关联用户
        QueryWrapper queryWrapper = QueryWrapper.create()
                .from("sys_user")
                .where("dept_id = ? AND deleted = 0", deptId);
        return mapper.selectCountByQuery(queryWrapper) > 0;
    }
    
    @Override
    public boolean moveDept(Long deptId, Long newParentId) {
        // 1. 验证部门是否存在
        SysDept dept = getById(deptId);
        if (dept == null) {
            throw new RuntimeException("部门不存在");
        }
        
        // 2. 验证新父部门不能是自己
        if (deptId.equals(newParentId)) {
            throw new RuntimeException("不能将部门移动到自己下面");
        }
        
        // 3. 验证新父部门不能是自己的子部门
        if (newParentId != 0 && isChildDept(deptId, newParentId)) {
            throw new RuntimeException("不能将部门移动到自己的子部门下");
        }
        
        // 4. 获取旧的ancestors
        String oldAncestors = dept.getAncestors();
        
        // 5. 计算新的ancestors
        String newAncestors;
        if (newParentId == 0) {
            newAncestors = "0";
        } else {
            SysDept newParent = getById(newParentId);
            if (newParent == null) {
                throw new RuntimeException("新父部门不存在");
            }
            newAncestors = newParent.getAncestors() + "," + newParentId;
        }
        
        // 6. 更新当前部门
        dept.setParentId(newParentId);
        dept.setAncestors(newAncestors);
        updateById(dept);
        
        // 7. 递归更新所有子部门的ancestors
        updateChildrenAncestors(deptId, oldAncestors, newAncestors);
        
        return true;
    }
    
    /**
     * 判断是否是子部门
     */
    private boolean isChildDept(Long parentId, Long childId) {
        SysDept child = getById(childId);
        if (child == null) {
            return false;
        }
        String ancestors = child.getAncestors();
        return ancestors != null && ancestors.contains("," + parentId + ",") 
                || ancestors.startsWith(parentId + ",")
                || ancestors.endsWith("," + parentId);
    }
    
    /**
     * 递归更新子部门的ancestors
     */
    private void updateChildrenAncestors(Long deptId, String oldAncestors, String newAncestors) {
        // 查询所有子部门
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_DEPT.PARENT_ID.eq(deptId))
                .and(SYS_DEPT.DELETED.eq(0));
        List<SysDept> children = list(queryWrapper);
        
        for (SysDept child : children) {
            // 替换ancestors中的旧路径为新路径
            String childAncestors = child.getAncestors();
            childAncestors = childAncestors.replace(oldAncestors, newAncestors);
            child.setAncestors(childAncestors);
            updateById(child);
            
            // 递归更新子部门的子部门
            updateChildrenAncestors(child.getId(), oldAncestors, newAncestors);
        }
    }
}
