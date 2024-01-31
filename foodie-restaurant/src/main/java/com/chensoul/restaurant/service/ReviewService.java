package com.chensoul.restaurant.service;

import com.chensoul.auth.client.UserClient;
import com.chensoul.auth.entity.User;
import com.chensoul.commons.constant.RedisKeyConstant;
import com.chensoul.commons.model.entity.Restaurant;
import com.chensoul.commons.model.entity.Review;
import com.chensoul.restaurant.ReviewVO;
import com.chensoul.restaurant.mapper.ReviewMapper;
import com.google.common.collect.Lists;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@AllArgsConstructor
public class ReviewService {
	private RedisTemplate redisTemplate;
	private RestaurantService restaurantService;
	private ReviewMapper reviewMapper;
	private UserClient userClient;
	private static final int NINE = 9;

	/**
	 * 添加餐厅评论
	 *
	 * @param restaurantId 餐厅ID
	 * @param accessToken  登录用户Token
	 * @param content      评论内容
	 * @param likeIt       是否喜欢
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addReview(final Long restaurantId, final String accessToken,
						  final String content, final int likeIt) {
		Assert.isTrue(restaurantId == null || restaurantId < 1, "请选择评论的餐厅");
		Assert.isNull(content, "请输入评论内容");
		Assert.isTrue(content.length() > 800, "评论内容过长，请重新输入");
		final Restaurant restaurant = this.restaurantService.findById(restaurantId);
		Assert.isTrue(restaurant == null, "该餐厅不存在");
		final User signInUserInfo = loadSignInUserInfo();
		final Review reviews = new Review();
		reviews.setContent(content);
		reviews.setUserId(signInUserInfo.getId());
		reviews.setRestaurantId(restaurantId);
		reviews.setLikeIt(likeIt);
		final int count = this.reviewMapper.saveReviews(reviews);
		if (count == 0) {
			return;
		}
		final String key = RedisKeyConstant.RESTAURANT_NEW_REVIEW.getKey() + restaurantId;
		this.redisTemplate.opsForList().leftPush(key, reviews);
	}

	/**
	 * 获取餐厅最新评论
	 *
	 * @param restaurantId 餐厅id
	 * @param accessToken  登录Token
	 * @return
	 */
	public List<ReviewVO> findNewReviews(final Long restaurantId, final String accessToken) {
		Assert.isTrue(restaurantId == null || restaurantId < 1, "请选择餐厅进行查看");
		final String key = RedisKeyConstant.RESTAURANT_NEW_REVIEW.getKey() + restaurantId;
		final List<LinkedHashMap> reviews = this.redisTemplate.opsForList().range(key, 0, NINE);
		// 初始化 VO 集合
		final List<ReviewVO> reviewsVOS = Lists.newArrayList();
		// 初始化用户 ID 集合
		final List<Long> userIds = Lists.newArrayList();
//		userClient.findByIds()

		return reviewsVOS;
	}

	/**
	 * 获取登录用户信息
	 *
	 * @return
	 */
	private User loadSignInUserInfo() {
		return userClient.getCurrentUser().getData();
	}

}
