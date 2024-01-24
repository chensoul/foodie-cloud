package com.imooc.restaurant.service;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.model.entity.Restaurant;
import com.imooc.restaurant.mapper.RestaurantMapper;
import java.util.LinkedHashMap;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class RestaurantService {

	@Resource
	public RestaurantMapper restaurantMapper;
	@Resource
	public RedisTemplate redisTemplate;

	/**
	 * 根据餐厅 ID 查询餐厅数据
	 *
	 * @param restaurantId
	 * @return
	 */
	public Restaurant findById(final Long restaurantId) {
		// 请选择餐厅
		Assert.isTrue(restaurantId != null, "请选择餐厅查看");
		// 获取 Key
		final String key = RedisKeyConstant.restaurant.getKey() + restaurantId;
		// 获取餐厅缓存
		final LinkedHashMap restaurantMap = (LinkedHashMap) this.redisTemplate.opsForHash().entries(key);
		// 如果缓存不存在，查询数据库
		Restaurant restaurant = null;
		if (restaurantMap == null || restaurantMap.isEmpty()) {
			log.info("缓存失效了，查询数据库：{}", restaurantId);
			// 查询数据库
			restaurant = this.restaurantMapper.findById(restaurantId);
			if (restaurant != null) {
				// 更新缓存
				this.redisTemplate.opsForHash().putAll(key, BeanUtils.beanToMap(restaurant));
			} else {
				// 写入缓存一个空数据，设置一个失效时间，60s
			}
		} else {
			restaurant = BeanUtils.mapToBean(restaurantMap,
				Restaurant.class);
		}
		return restaurant;
	}

}
