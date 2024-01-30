package com.imooc.feed.service;

import com.google.common.collect.Lists;
import com.imooc.auth.client.UserClient;
import com.imooc.auth.model.LoggedUser;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.Feed;
import com.imooc.feed.FeedVO;
import com.imooc.feed.mapper.FeedMapper;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
public class FeedService {

	@Value("${service.name.foodie-follow}")
	private String followServerName;
	@Value("${service.name.foodie-user}")
	private String userServerName;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private FeedMapper feedMapper;
	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private UserClient userClient;

	public List<FeedVO> selectForPage(Integer page) {
		if (page == null) {
			page = 1;
		}
		final LoggedUser userInfo = loadSignInUserInfo();
		final String key = RedisKeyConstant.FOLLOWING_FEED.getKey() + userInfo.getId();
		final long start = (page - 1) * ApiConstant.PAGE_SIZE;
		final long end = page * ApiConstant.PAGE_SIZE - 1;
		final Set<Long> feedIds = this.redisTemplate.opsForZSet().reverseRange(key, start, end);
		if (feedIds == null || feedIds.isEmpty()) {
			return Lists.newArrayList();
		}

		final List<Feed> feeds = this.feedMapper.findByIds(feedIds);
		final List<Long> followingUserIds = new ArrayList<>();
		final List<FeedVO> feedVOS = feeds.stream().map(feed -> {
			final FeedVO feedVO = new FeedVO();
			BeanUtils.copyProperties(feed, feedVO);
			followingUserIds.add(feed.getUserId());
			return feedVO;
		}).collect(Collectors.toList());

		final R resultInfo = this.restTemplate.getForObject(this.userServerName + "findByIds?access_token=${accessToken}&ids={ids}",
			R.class, followingUserIds);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		return feedVOS;
	}

	/**
	 * 变更 Feed
	 *
	 * @param followingUserId 关注的好友 ID
	 * @param type            1 关注 0 取关
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addFollowingFeed(final Long followingUserId, final int type) {
		Assert.isTrue(followingUserId != null && followingUserId >= 1,
			"请选择关注的好友");
		final LoggedUser userInfo = loadSignInUserInfo();
		final List<Feed> feedList = this.feedMapper.findByUserId(followingUserId);
		final String key = RedisKeyConstant.FOLLOWING_FEED.getKey() + userInfo.getId();
		if (type == 0) {
			final List<Long> feedIds = feedList.stream()
				.map(feed -> feed.getId())
				.collect(Collectors.toList());
			this.redisTemplate.opsForZSet().remove(key, feedIds.toArray(new Integer[]{}));
		} else {
			final Set<ZSetOperations.TypedTuple> typedTuples =
				feedList.stream()
					.map(feed -> new DefaultTypedTuple<>(feed.getId(), (double)
						feed.getUpdateTime().toEpochSecond(ZoneOffset.of(ZoneOffset.systemDefault().getId()))))
					.collect(Collectors.toSet());
			this.redisTemplate.opsForZSet().add(key, typedTuples);
		}
	}

	/**
	 * 删除 Feed
	 *
	 * @param id
	 */
	@Transactional(rollbackFor = Exception.class)
	public void delete(final Long id) {
		final LoggedUser userInfo = loadSignInUserInfo();
		final Feed feed = this.feedMapper.findById(id);
		Assert.isTrue(feed != null, "该Feed已被删除");
		Assert.isTrue(!feed.getUserId().equals(userInfo.getId()),
			"只能删除自己的Feed");
		final int count = this.feedMapper.delete(id);
		if (count == 0) {
			return;
		}
		final List<Integer> followers = this.findFollower(userInfo.getId());
		followers.forEach(follower -> {
			final String key = RedisKeyConstant.FOLLOWING_FEED.getKey() + follower;
			this.redisTemplate.opsForZSet().remove(key, feed.getId());
		});
	}

	/**
	 * 添加 Feed
	 *
	 * @param feed
	 */
	@Transactional(rollbackFor = Exception.class)
	public void create(final Feed feed) {
		Assert.hasLength(feed.getContent(), "请输入内容");
		Assert.isTrue(feed.getContent().length() <= 255, "输入内容太多，请重新输入");
		final LoggedUser userInfo = loadSignInUserInfo();
		feed.setUserId(userInfo.getId());
		final int count = this.feedMapper.save(feed);
		Assert.isTrue(count != 0, "添加失败");
		final List<Integer> followers = this.findFollower(userInfo.getId());
		final long now = System.currentTimeMillis();
		followers.forEach(follower -> {
			final String key = RedisKeyConstant.FOLLOWING_FEED.getKey() + follower;
			this.redisTemplate.opsForZSet().add(key, feed.getId(), now);
		});
	}

	/**
	 * 获取粉丝 id 集合
	 *
	 * @param userId
	 * @return
	 */
	private List<Integer> findFollower(final Long userId) {
		final String url = this.followServerName + "follower/" + userId;
		final R resultInfo = this.restTemplate.getForObject(url, R.class);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		final List<Integer> followers = (List<Integer>) resultInfo.getData();
		return followers;
	}

	/**
	 * 获取登录用户信息
	 *
	 * @return
	 */
	private LoggedUser loadSignInUserInfo() {
		return userClient.getCurrentUser().getData();
	}

}
