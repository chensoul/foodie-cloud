package com.chensoul;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.chensoul.domain.**.mapper")
@EnableFeignClients
public class PointApplication {

	public static void main(final String[] args) {
		SpringApplication.run(PointApplication.class, args);
	}

}
