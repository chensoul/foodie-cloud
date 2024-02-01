package com.chensoul.foodie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableResourceServer
@MapperScan("com.chensoul.foodie.domain.**.mapper")
@SpringBootApplication
public class RestaurantApplication {

	public static void main(final String[] args) {
		SpringApplication.run(RestaurantApplication.class, args);
	}

}
