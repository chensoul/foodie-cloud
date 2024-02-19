package com.chensoul.domain.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chensoul.client.UserClient;
import com.chensoul.constant.RedisKeyConstant;
import com.chensoul.domain.restaurant.entity.Restaurant;
import com.chensoul.domain.restaurant.entity.Review;
import com.chensoul.domain.restaurant.mapper.ReviewMapper;
import com.chensoul.domain.restaurant.model.ReviewVO;
import com.chensoul.domain.user.entity.User;
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
public class ReviewService extends ServiceImpl<ReviewMapper, Review> implements IService<Review> {
	private RedisTemplate redisTemplate;
	private RestaurantService restaurantService;
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
		final User signInUserInfo = this.loadSignInUserInfo();
		final Review reviews = new Review();
		reviews.setContent(content);
		reviews.setUserId(signInUserInfo.getId());
		reviews.setRestaurantId(restaurantId);
		reviews.setLikeIt(likeIt);
		final int count = this.baseMapper.insert(reviews);
		if (count == 0){
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
		return this.userClient.getCurrentUser().getData();
	}

}
