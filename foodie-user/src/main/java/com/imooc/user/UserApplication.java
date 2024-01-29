package com.imooc.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@MapperScan("com.imooc.user.mapper")
@SpringBootApplication
@EnableResourceServer
public class UserApplication {
	public static void main(final String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
