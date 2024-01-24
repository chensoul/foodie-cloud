package com.imooc.restaurant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.imooc.restaurant.mapper")
@SpringBootApplication
public class RestaurantApplication {

	public static void main(final String[] args) {
		SpringApplication.run(RestaurantApplication.class, args);
	}

}
