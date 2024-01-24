package com.imooc.feed.service;

import com.google.common.collect.Lists;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.Diner;
import com.imooc.commons.model.entity.Feed;
import com.imooc.commons.model.vo.FeedVO;
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

	@Value("${service.name.foodie-oauth-server}")
	private String oauthServerName;
	@Value("${service.name.foodie-follow-server}")
	private String followServerName;
	@Value("${service.name.foodie-diner-server}")
	private String dinerServerName;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private FeedMapper feedMapper;
	@Resource
	private RedisTemplate redisTemplate;

	public List<FeedVO> selectForPage(Integer page, final String accessToken) {
		if (page == null) {
			page = 1;
		}
		// 获取登录用户
		final Diner dinerInfo = this.loadSignInDinerInfo(accessToken);
		// 我关注的好友的 Feedkey
		final String key = RedisKeyConstant.following_feed.getKey() + dinerInfo.getId();
		// SortedSet 的 ZREVRANGE 命令是闭区间
		final long start = (page - 1) * ApiConstant.PAGE_SIZE;
		final long end = page * ApiConstant.PAGE_SIZE - 1;
		final Set<Long> feedIds = this.redisTemplate.opsForZSet().reverseRange(key, start, end);
		if (feedIds == null || feedIds.isEmpty()) {
			return Lists.newArrayList();
		}
		final List<Feed> feeds = this.feedMapper.findByIds(feedIds);
		final List<Long> followingDinerIds = new ArrayList<>();
		// 添加用户 ID 至集合，顺带将 Feeds 转为 Vo 对象
		final List<FeedVO> feedVOS = feeds.stream().map(feed -> {
			final FeedVO feedVO = new FeedVO();
			BeanUtils.copyProperties(feed, feedVO);
			// 添加用户 ID
			followingDinerIds.add(feed.getDinerId());
			return feedVO;
		}).collect(Collectors.toList());
		// 远程调用获取 Feed 中用户信息
		final R resultInfo = this.restTemplate.getForObject(this.dinerServerName + "findByIds?access_token=${accessToken}&ids={ids}",
			R.class, accessToken, followingDinerIds);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		return feedVOS;
	}

	/**
	 * 变更 Feed
	 *
	 * @param followingDinerId 关注的好友 ID
	 * @param accessToken      登录用户token
	 * @param type             1 关注 0 取关
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addFollowingFeed(final Long followingDinerId, final String accessToken, final int type) {
		// 请选择关注的好友
		Assert.isTrue(followingDinerId != null && followingDinerId >= 1,
			"请选择关注的好友");
		// 获取登录用户信息
		final Diner dinerInfo = this.loadSignInDinerInfo(accessToken);
		// 获取关注/取关的食客的所有 Feed
		final List<Feed> feedList = this.feedMapper.findByDinerId(followingDinerId);
		final String key = RedisKeyConstant.following_feed.getKey() + dinerInfo.getId();
		if (type == 0) {
			// 取关
			final List<Long> feedIds = feedList.stream()
				.map(feed -> feed.getId())
				.collect(Collectors.toList());
			this.redisTemplate.opsForZSet().remove(key, feedIds.toArray(new Integer[]{}));
		} else {
			// 关注
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
	 * @param accessToken
	 */
	@Transactional(rollbackFor = Exception.class)
	public void delete(final Long id, final String accessToken) {
		// 获取登录用户
		final Diner dinerInfo = this.loadSignInDinerInfo(accessToken);
		// 获取 Feed 内容
		final Feed feed = this.feedMapper.findById(id);
		// 判断 Feed 是否已经被删除且只能删除自己的 Feed
		Assert.isTrue(feed != null, "该Feed已被删除");
		Assert.isTrue(!feed.getDinerId().equals(dinerInfo.getId()),
			"只能删除自己的Feed");
		// 删除
		final int count = this.feedMapper.delete(id);
		if (count == 0) {
			return;
		}
		// 将内容从粉丝的集合中删除 -- 异步消息队列优化
		// 先获取我的粉丝
		final List<Integer> followers = this.findFollower(dinerInfo.getId());
		// 移除 Feed
		followers.forEach(follower -> {
			final String key = RedisKeyConstant.following_feed.getKey() + follower;
			this.redisTemplate.opsForZSet().remove(key, feed.getId());
		});
	}

	/**
	 * 添加 Feed
	 *
	 * @param feed
	 * @param accessToken
	 */
	@Transactional(rollbackFor = Exception.class)
	public void create(final Feed feed, final String accessToken) {
		// 校验 Feed 内容不能为空，不能太长
		Assert.hasLength(feed.getContent(), "请输入内容");
		Assert.isTrue(feed.getContent().length() <= 255, "输入内容太多，请重新输入");
		// 获取登录用户信息
		final Diner dinerInfo = this.loadSignInDinerInfo(accessToken);
		// Feed 关联用户信息
		feed.setDinerId(dinerInfo.getId());
		// 添加 Feed
		final int count = this.feedMapper.save(feed);
		Assert.isTrue(count != 0, "添加失败");
		// 推送到粉丝的列表中 -- 后续这里应该采用异步消息队列解决性能问题
		// 先获取粉丝 id 集合
		final List<Integer> followers = this.findFollower(dinerInfo.getId());
		// 推送 Feed
		final long now = System.currentTimeMillis();
		followers.forEach(follower -> {
			final String key = RedisKeyConstant.following_feed.getKey() + follower;
			this.redisTemplate.opsForZSet().add(key, feed.getId(), now);
		});
	}

	/**
	 * 获取粉丝 id 集合
	 *
	 * @param dinerId
	 * @return
	 */
	private List<Integer> findFollower(final Long dinerId) {
		final String url = this.followServerName + "follower/" + dinerId;
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
	 * @param accessToken
	 * @return
	 */
	private Diner loadSignInDinerInfo(final String accessToken) {
		// 必须登录
		final String url = this.oauthServerName + "user/me?access_token={accessToken}";
		final R resultInfo = this.restTemplate.getForObject(url, R.class, accessToken);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		return null;
	}

}
