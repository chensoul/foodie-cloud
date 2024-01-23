package com.imooc.restaurants.service;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Maps;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.model.pojo.Restaurant;
import com.imooc.restaurants.mapper.RestaurantMapper;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
public class RestaurantTest {

	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private RestaurantMapper restaurantMapper;

	// 逐行插入
	@Test
	void testSyncForHash() {
		final List<Restaurant> restaurants = this.restaurantMapper.findAll();
		final long start = System.currentTimeMillis();
		restaurants.forEach(restaurant -> {
			final Map<String, Object> restaurantMap = BeanUtil.beanToMap(restaurant);
			final String key = RedisKeyConstant.restaurants.getKey() + restaurant.getId();
			this.redisTemplate.opsForHash().putAll(key, restaurantMap);
		});
		final long end = System.currentTimeMillis();
		log.info("执行时间：{}", end - start); // 执行时间：118957
	}

	// Pipeline 管道插入
	@Test
	void testSyncForHashPipeline() {
		final List<Restaurant> restaurants = this.restaurantMapper.findAll();
		final long start = System.currentTimeMillis();
		final List<Long> list = this.redisTemplate.executePipelined((RedisCallback<Long>) connection -> {
			for (final Restaurant restaurant : restaurants) {
				try {
					final String key = RedisKeyConstant.restaurants.getKey() + restaurant.getId();
					final Map<String, Object> restaurantMap = BeanUtil.beanToMap(restaurant);
					final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
					final Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
					final Map<byte[], byte[]> restaurantStringMap = Maps.newHashMap();
					restaurantMap.forEach((k, v) -> {
						restaurantStringMap.put(stringRedisSerializer.serialize(k), jackson2JsonRedisSerializer.serialize(v));
					});
					connection.hMSet(stringRedisSerializer.serialize(key), restaurantStringMap);
				} catch (final Exception e) {
					log.info(restaurant.toString());
					continue;
				}
			}
			return null;
		});
		final long end = System.currentTimeMillis();
		log.info("执行时间：{}", end - start); // 执行时间：35606
	}

}
