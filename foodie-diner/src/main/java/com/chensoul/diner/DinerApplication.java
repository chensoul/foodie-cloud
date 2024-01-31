package com.chensoul.diner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
@EnableFeignClients(basePackages = {"com.chensoul.auth"})
public class DinerApplication {
	public static void main(final String[] args) {
		SpringApplication.run(DinerApplication.class, args);
	}

}
