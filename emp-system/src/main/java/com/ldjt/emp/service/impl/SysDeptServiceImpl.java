//package com.ldjt.emp.service.impl;
//
//import com.ldjt.emp.entity.SysDept;
//import com.ldjt.emp.mapper.SysDeptMapper;
//import com.ldjt.emp.service.SysDeptService;
//import com.mybatisflex.core.query.QueryWrapper;
//import com.mybatisflex.spring.service.impl.ServiceImpl;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//import static com.ldjt.emp.entity.table.SysDeptTableDef.SYS_DEPT;
//
///**
// * 部门服务实现类
// *
// * @author emp
// */
//@Service
//public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {
//
//    @Override
//    public List<SysDept> getDeptTree() {
//        QueryWrapper queryWrapper = QueryWrapper.create()
//                .where(SYS_DEPT.DELETED.eq(0))
//                .orderBy(SYS_DEPT.ORDER_NUM.asc());
//
//        return this.list(queryWrapper);
//    }
//}
