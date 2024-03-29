package com.chensoul.controller;

import com.chensoul.core.model.R;
import com.chensoul.domain.restaurant.entity.Restaurant;
import com.chensoul.domain.restaurant.service.RestaurantService;
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
		final Restaurant restaurant = this.restaurantService.findById(restaurantId);
		return R.ok(restaurant);
	}

}
