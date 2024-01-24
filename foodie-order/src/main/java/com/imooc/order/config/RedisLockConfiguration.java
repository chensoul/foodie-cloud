package com.imooc.order.config;

import com.imooc.order.model.RedisLock;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisLockConfiguration {

	@Resource
	private RedisTemplate redisTemplate;

	@Bean
	public RedisLock redisLock() {
		final RedisLock redisLock = new RedisLock(this.redisTemplate);
		return redisLock;
	}
}
