package com.imooc.restaurant.mapper;

import com.imooc.commons.model.entity.Review;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface ReviewMapper {

	// 插入餐厅评论
	@Insert("insert into t_review (restaurant_id, user_id, content, like_it,  create_time, update_time)" +
			" values (#{restaurantId}, #{userId}, #{content}, #{likeIt},  now(), now())")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	int saveReviews(Review review);

}
