package com.ldjt.emp.mapper;

import com.ldjt.emp.entity.Account;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账户Mapper接口
 *
 * @author wdf
 * @since 2025/9/27 16:03
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {
    // BaseMapper已提供基础的CRUD方法
    // 可以在这里添加自定义查询方法
}
