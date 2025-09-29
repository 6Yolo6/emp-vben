package com.ldjt.emp;

import com.ldjt.emp.entity.Account;
import com.ldjt.emp.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.ldjt.emp.entity.table.AccountTableDef.ACCOUNT;

@SpringBootTest
class MyBatisFlexTest {

    @Autowired
    private AccountMapper accountMapper;

    @Test
    void testSelectAll() {
        System.out.println("=== 查询所有账户 ===");
        List<Account> accounts = accountMapper.selectAll();
        accounts.forEach(System.out::println);
    }

    @Test
    void testSelectById() {
        System.out.println("=== 根据ID查询账户 ===");
        Account account = accountMapper.selectOneById(1L);
        System.out.println(account);
    }

    @Test
    void testSelectByAge() {
        System.out.println("=== 根据年龄查询账户 ===");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .where(ACCOUNT.AGE.eq(18));
        Account account = accountMapper.selectOneByQuery(queryWrapper);
        System.out.println(account);
    }

    @Test
    void testInsert() {
        System.out.println("=== 插入新账户 ===");
        Account newAccount = new Account();
        newAccount.setUserName("测试用户");
        newAccount.setAge(25);
        newAccount.setBirthday(LocalDateTime.now());

        accountMapper.insert(newAccount);
        System.out.println("插入成功，ID: " + newAccount.getId());
    }

    @Test
    void testUpdate() {
        System.out.println("=== 更新账户信息 ===");
        Account account = accountMapper.selectOneById(1L);
        if (account != null) {
            account.setAge(19);
            accountMapper.update(account);
            System.out.println("更新成功: " + account);
        }
    }

    @Test
    void testDelete() {
        System.out.println("=== 删除账户 ===");
        // 先插入一个测试账户
        Account testAccount = new Account();
        testAccount.setUserName("待删除用户");
        testAccount.setAge(30);
        testAccount.setBirthday(LocalDateTime.now());
        accountMapper.insert(testAccount);

        // 删除刚插入的账户
        accountMapper.deleteById(testAccount.getId());
        System.out.println("删除成功，ID: " + testAccount.getId());
    }

    @Test
    void testComplexQuery() {
        System.out.println("=== 复杂查询示例 ===");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .where(ACCOUNT.AGE.ge(18))  // 年龄大于等于18
                .and(ACCOUNT.USER_NAME.like("张%"))  // 姓名以"张"开头
                .orderBy(ACCOUNT.AGE.desc());  // 按年龄降序

        List<Account> accounts = accountMapper.selectListByQuery(queryWrapper);
        accounts.forEach(System.out::println);
    }
}
