package com.chensoul;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.chensoul.domain.**.mapper")
@SpringBootApplication
public class AuthApplication {

	public static void main(final String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}
