package com.chensoul.follow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableResourceServer
@SpringBootApplication
@MapperScan("com.chensoul.follow.**.mapper")
@EnableFeignClients(basePackages = {"com.chensoul.auth"})
public class FollowApplication {

	public static void main(final String[] args) {
		SpringApplication.run(FollowApplication.class, args);
	}

}
