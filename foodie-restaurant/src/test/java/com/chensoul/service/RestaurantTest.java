package com.chensoul.service;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chensoul.RestaurantApplication;
import com.chensoul.constant.RedisKeyConstant;
import com.chensoul.domain.restaurant.entity.Restaurant;
import com.chensoul.domain.restaurant.mapper.RestaurantMapper;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@AllArgsConstructor
@SpringBootTest(classes = RestaurantApplication.class)
public class RestaurantTest {
	private RedisTemplate redisTemplate;

	private RestaurantMapper restaurantMapper;

	//	@Test
	void testSyncForHash() {
		final List<Restaurant> restaurants = this.restaurantMapper.selectList(Wrappers.lambdaQuery());
		final long start = System.currentTimeMillis();
		restaurants.forEach(restaurant -> {
			final Map<String, Object> restaurantMap = BeanUtils.beanToMap(restaurant);
			final String key = RedisKeyConstant.RESTAURANT.getKey() + restaurant.getId();
			this.redisTemplate.opsForHash().putAll(key, restaurantMap);
		});
		final long end = System.currentTimeMillis();
		log.info("执行时间：{}", end - start); // 执行时间：118957
	}

	//	@Test
	void testSyncForHashPipeline() {
		final List<Restaurant> restaurants = this.restaurantMapper.selectList(Wrappers.lambdaQuery());
		final long start = System.currentTimeMillis();
		final List<Long> list = this.redisTemplate.executePipelined((RedisCallback<Long>) connection -> {
			for (final Restaurant restaurant : restaurants)
				try {
					final String key = RedisKeyConstant.RESTAURANT.getKey() + restaurant.getId();
					final Map<String, Object> restaurantMap = BeanUtils.beanToMap(restaurant);
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
			return null;
		});
		final long end = System.currentTimeMillis();
		log.info("执行时间：{}", end - start); // 执行时间：35606
	}

}
