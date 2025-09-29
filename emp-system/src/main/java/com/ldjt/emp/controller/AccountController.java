package com.ldjt.emp.controller;

import com.ldjt.emp.entity.Account;
import com.ldjt.emp.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ldjt.emp.entity.table.AccountTableDef.ACCOUNT;

/**
 * 账户管理Controller，测试Mybatis-Flex的使用
 *
 * @author wdf
 * @since 2025/9/27 16:03
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 查询所有账户
     */
    @GetMapping("/list")
    public List<Account> getAllAccounts() {
        return accountMapper.selectAll();
    }

    /**
     * 根据ID查询账户
     */
    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountMapper.selectOneById(id);
    }

    /**
     * 根据年龄查询账户
     */
    @GetMapping("/age/{age}")
    public List<Account> getAccountsByAge(@PathVariable Integer age) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .where(ACCOUNT.AGE.eq(age));
        return accountMapper.selectListByQuery(queryWrapper);
    }

    /**
     * 创建新账户
     */
    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        accountMapper.insert(account);
        return account;
    }

    /**
     * 更新账户
     */
    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) {
        account.setId(id);
        accountMapper.update(account);
        return account;
    }

    /**
     * 删除账户
     */
    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id) {
        accountMapper.deleteById(id);
        return "Account deleted successfully";
    }
}
