package com.chensoul;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.chensoul.domain.**.mapper")
@EnableFeignClients
@SpringBootApplication
public class RestaurantApplication {

	public static void main(final String[] args) {
		SpringApplication.run(RestaurantApplication.class, args);
	}

}
