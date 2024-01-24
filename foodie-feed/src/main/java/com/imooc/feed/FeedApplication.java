package com.imooc.feed;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.imooc.feed.mapper")
@SpringBootApplication
public class FeedApplication {

	public static void main(final String[] args) {
		SpringApplication.run(FeedApplication.class, args);
	}

}
