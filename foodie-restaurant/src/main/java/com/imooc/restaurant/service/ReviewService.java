package com.imooc.restaurant.service;

import com.google.common.collect.Lists;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.User;
import com.imooc.commons.model.entity.Restaurant;
import com.imooc.commons.model.entity.Review;
import com.imooc.commons.model.vo.ReviewVO;
import com.imooc.restaurant.mapper.ReviewMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
public class ReviewService {

	@Value("${service.name.ms-oauth-server}")
	private String oauthServerName;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private RestaurantService restaurantService;
	@Resource
	private ReviewMapper reviewMapper;

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
		// 参数校验
		Assert.isTrue(restaurantId == null || restaurantId < 1, "请选择评论的餐厅");
		Assert.isNull(content, "请输入评论内容");
		Assert.isTrue(content.length() > 800, "评论内容过长，请重新输入");
		// 判断餐厅是否存在
		final Restaurant restaurant = this.restaurantService.findById(restaurantId);
		Assert.isTrue(restaurant == null, "该餐厅不存在");
		// 获取登录用户信息
		final User signInUserInfo = this.loadSignInDinerInfo(accessToken);
		// 插入数据库
		final Review reviews = new Review();
		reviews.setContent(content);
		reviews.setDinerId(signInUserInfo.getId());
		reviews.setRestaurantId(restaurantId);
		// 这里需要后台操作处理餐厅数据(喜欢/不喜欢餐厅)做自增处理
		reviews.setLikeIt(likeIt);
		final int count = this.reviewMapper.saveReviews(reviews);
		if (count == 0) {
			return;
		}
		// 写入餐厅最新评论
		final String key = RedisKeyConstant.restaurant_new_review.getKey() + restaurantId;
		this.redisTemplate.opsForList().leftPush(key, reviews);
		// 保证队列中只需要十条 作业
	}

	@Value("${service.name.ms-diners-server}")
	private String dinersServerName;
	private static final int NINE = 9;

	/**
	 * 获取餐厅最新评论
	 *
	 * @param restaurantId 餐厅id
	 * @param accessToken  登录Token
	 * @return
	 */
	public List<ReviewVO> findNewReviews(final Long restaurantId, final String accessToken) {
		// 参数校验
		Assert.isTrue(restaurantId == null || restaurantId < 1, "请选择餐厅进行查看");
		// 获取 Key
		final String key = RedisKeyConstant.restaurant_new_review.getKey() + restaurantId;
		// 取前十条
		final List<LinkedHashMap> reviews = this.redisTemplate.opsForList().range(key, 0, NINE);
		// 初始化 VO 集合
		final List<ReviewVO> reviewsVOS = Lists.newArrayList();
		// 初始化用户 ID 集合
		final List<Integer> dinerIds = Lists.newArrayList();
		// 循环处理评论集合
		// 查询评论用户信息
		final R resultInfo = this.restTemplate.getForObject(this.dinersServerName +
															"findByIds?access_token=${accessToken}&ids={ids}",
			R.class, accessToken, StringUtils.join(",", dinerIds));
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		final List<LinkedHashMap> dinerInfoMaps = (ArrayList) resultInfo.getData();

		return reviewsVOS;
	}

	/**
	 * 获取登录用户信息
	 *
	 * @param accessToken
	 * @return
	 */
	private User loadSignInDinerInfo(final String accessToken) {
		// 获取登录用户信息
		final String url = this.oauthServerName + "user/me?access_token={accessToken}";
		final R resultInfo = this.restTemplate.getForObject(url, R.class, accessToken);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}

		return null;
	}

}
