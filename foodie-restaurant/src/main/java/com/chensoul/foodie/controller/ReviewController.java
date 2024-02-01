package com.chensoul.foodie.controller;

import com.chensoul.core.model.R;
import com.chensoul.foodie.domain.restaurant.model.ReviewVO;
import com.chensoul.foodie.domain.restaurant.service.ReviewService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews")
public class ReviewController {


	private ReviewService reviewService;

	/**
	 * 添加餐厅评论
	 *
	 * @param restaurantId
	 * @param access_token
	 * @param content
	 * @param likeIt
	 * @return
	 */
	@PostMapping("{restaurantId}")
	public R<String> addReview(@PathVariable final Long restaurantId,
							   final String access_token,
							   @RequestParam("content") final String content,
							   @RequestParam("likeIt") final int likeIt) {
		reviewService.addReview(restaurantId, access_token, content, likeIt);
		return R.ok();
	}

	/**
	 * 获取餐厅最新评论
	 *
	 * @param restaurantId
	 * @param access_token
	 * @return
	 */
	@GetMapping("{restaurantId}/new")
	public R<List<ReviewVO>> findNewReview(@PathVariable final Long restaurantId,
										   final String access_token) {
		final List<ReviewVO> reviewsList = reviewService.findNewReviews(restaurantId, access_token);
		return R.ok(reviewsList);
	}

}
