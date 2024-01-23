package com.imooc.restaurants.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.vo.ReviewsVO;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.restaurants.service.ReviewsService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews")
public class ReviewsController {

	@Resource
	private ReviewsService reviewsService;

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
	public ResultInfo<String> addReview(@PathVariable final Integer restaurantId,
										final String access_token,
										@RequestParam("content") final String content,
										@RequestParam("likeIt") final int likeIt) {
		this.reviewsService.addReview(restaurantId, access_token, content, likeIt);
		return ResultInfoUtil.buildSuccess("添加成功");
	}

	/**
	 * 获取餐厅最新评论
	 *
	 * @param restaurantId
	 * @param access_token
	 * @return
	 */
	@GetMapping("{restaurantId}/news")
	public ResultInfo<List<ReviewsVO>> findNewReviews(@PathVariable final Integer restaurantId,
													  final String access_token) {
		final List<ReviewsVO> reviewsList = this.reviewsService.findNewReviews(restaurantId, access_token);
		return ResultInfoUtil.buildSuccess(reviewsList);
	}

}
