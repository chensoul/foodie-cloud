package com.imooc.point;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.imooc.point.mapper")
@SpringBootApplication
public class PointApplication {

	public static void main(final String[] args) {
		SpringApplication.run(PointApplication.class, args);
	}

}
