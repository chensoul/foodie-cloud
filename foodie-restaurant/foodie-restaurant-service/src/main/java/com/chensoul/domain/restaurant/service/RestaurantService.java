package com.chensoul.domain.restaurant.service;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chensoul.constant.RedisKeyConstant;
import com.chensoul.domain.restaurant.entity.Restaurant;
import com.chensoul.domain.restaurant.mapper.RestaurantMapper;
import java.util.LinkedHashMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
@AllArgsConstructor
public class RestaurantService extends ServiceImpl<RestaurantMapper, Restaurant> implements IService<Restaurant> {
	public RedisTemplate redisTemplate;

	/**
	 * 根据餐厅 ID 查询餐厅数据
	 *
	 * @param restaurantId
	 * @return
	 */
	public Restaurant findById(final Long restaurantId) {
		Assert.isTrue(restaurantId != null, "请选择餐厅查看");
		final String key = RedisKeyConstant.RESTAURANT.getKey() + restaurantId;
		final LinkedHashMap restaurantMap = (LinkedHashMap) this.redisTemplate.opsForHash().entries(key);
		Restaurant restaurant = null;
		if (restaurantMap == null || restaurantMap.isEmpty()) {
			log.info("缓存失效了，查询数据库：{}", restaurantId);
			restaurant = this.baseMapper.selectById(restaurantId);
			if (restaurant != null) {
				this.redisTemplate.opsForHash().putAll(key, BeanUtils.beanToMap(restaurant));
			} else {
				// 写入缓存一个空数据，设置一个失效时间，60s
			}
		} else restaurant = BeanUtils.mapToBean(restaurantMap,
			Restaurant.class);
		return restaurant;
	}

}
