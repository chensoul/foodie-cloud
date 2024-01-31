package com.chensoul.restaurant.controller;

import com.chensoul.commons.model.domain.R;
import com.chensoul.commons.model.entity.Restaurant;
import com.chensoul.restaurant.domain.service.RestaurantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestaurantController {


	private RestaurantService restaurantService;

	/**
	 * 根据餐厅 ID 查询餐厅数据
	 *
	 * @param restaurantId
	 * @return
	 */
	@GetMapping("detail/{restaurantId}")
	public R<Restaurant> findById(@PathVariable final Long restaurantId) {
		final Restaurant restaurant = restaurantService.findById(restaurantId);
		return R.ok(restaurant);
	}

}
