package com.chensoul.domain.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chensoul.client.UserClient;
import com.chensoul.constant.RedisKeyConstant;
import com.chensoul.core.constant.Constant;
import com.chensoul.domain.feed.entity.Feed;
import com.chensoul.domain.feed.model.FeedVO;
import com.chensoul.domain.mapper.FeedMapper;
import com.chensoul.domain.user.entity.User;
import com.google.common.collect.Lists;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@AllArgsConstructor
public class FeedService extends ServiceImpl<FeedMapper, Feed> implements IService<Feed> {
	private RedisTemplate redisTemplate;
	private UserClient userClient;

	public List<FeedVO> selectForPage(Integer page) {
		if (page == null){
			page = 1;
		}
		final User userInfo = this.loadSignInUserInfo();
		final String key = RedisKeyConstant.FOLLOWING_FEED.getKey() + userInfo.getId();
		final long start = (page - 1) * Constant.PAGE_SIZE;
		final long end = page * Constant.PAGE_SIZE - 1;
		final Set<Long> feedIds = this.redisTemplate.opsForZSet().reverseRange(key, start, end);
		if (feedIds == null || feedIds.isEmpty()) {
			return Lists.newArrayList();
		}

		final List<Feed> feeds = this.baseMapper.selectBatchIds(feedIds);
		final List<Long> followingUserIds = new ArrayList<>();
		final List<FeedVO> feedVOS = feeds.stream().map(feed -> {
			final FeedVO feedVO = new FeedVO();
			BeanUtils.copyProperties(feed, feedVO);
			followingUserIds.add(feed.getUserId());
			return feedVO;
		}).collect(Collectors.toList());


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
		final User userInfo = this.loadSignInUserInfo();
		final List<Feed> feedList = this.baseMapper.selectList(Wrappers.<Feed>lambdaQuery().eq(Feed::getUserId, followingUserId));

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
		final User userInfo = this.loadSignInUserInfo();
		final Feed feed = this.baseMapper.selectById(id);
		Assert.isTrue(feed != null, "该Feed已被删除");
		Assert.isTrue(!feed.getUserId().equals(userInfo.getId()), "只能删除自己的Feed");
		final int count = this.baseMapper.deleteById(id);
		if (count == 0) {
			return;
		}
		final List<Integer> followers = FeedService.findFollower(userInfo.getId());
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
		final User userInfo = this.loadSignInUserInfo();
		feed.setUserId(userInfo.getId());
		final int count = this.baseMapper.insert(feed);
		Assert.isTrue(count != 0, "添加失败");
		final List<Integer> followers = FeedService.findFollower(userInfo.getId());
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
	private static List<Integer> findFollower(final Long userId) {
//		final String url = followServerName + "follower/" + userId;
//		final R resultInfo = restTemplate.getForObject(url, R.class);
//		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
//			throw new IllegalArgumentException(resultInfo.getMessage());
//		}
//		final List<Integer> followers = (List<Integer>) resultInfo.getData();
		return null;
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
