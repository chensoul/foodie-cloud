package com.chensoul.foodie.infrastructure.lock;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@AllArgsConstructor
public class RedisLockConfiguration {

	private RedisTemplate redisTemplate;

	@Bean
	public RedisLock redisLock() {
		final RedisLock redisLock = new RedisLock(redisTemplate);
		return redisLock;
	}
}
