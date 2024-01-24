package com.imooc.diner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.imooc.diner.mapper")
@SpringBootApplication
public class DinerApplication {
	public static void main(final String[] args) {
		SpringApplication.run(DinerApplication.class, args);
	}

}
