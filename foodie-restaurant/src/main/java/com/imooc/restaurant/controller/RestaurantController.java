package com.imooc.restaurant.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.Restaurant;
import com.imooc.restaurant.service.RestaurantService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestaurantController {

	@Resource
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
