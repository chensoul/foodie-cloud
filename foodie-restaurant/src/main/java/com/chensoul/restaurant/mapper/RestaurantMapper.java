package com.chensoul.restaurant.mapper;

import com.chensoul.commons.model.entity.Restaurant;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RestaurantMapper {

	// 查询餐厅信息
	@Select("select * from t_restaurant")
	List<Restaurant> findAll();

	// 根据餐厅 ID 查询餐厅信息
	@Select("select * from t_restaurant where id = #{id}")
	Restaurant findById(@Param("id") Long id);

}
