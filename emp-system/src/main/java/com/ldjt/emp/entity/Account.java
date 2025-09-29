package com.ldjt.emp.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账户实体类
 *
 * @author wdf
 * @since 2025/9/27 16:03
 */
@Data
@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;
    private Integer age;
    private LocalDateTime birthday;
}

