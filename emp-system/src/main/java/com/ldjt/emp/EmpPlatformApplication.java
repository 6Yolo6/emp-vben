package com.ldjt.emp;

import cn.dev33.satoken.SaManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ldjt.emp.mapper")  // 扫描Mapper接口
public class EmpPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmpPlatformApplication.class, args);
		System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
	}

}
