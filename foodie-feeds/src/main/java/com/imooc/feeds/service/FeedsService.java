package com.imooc.feeds.service;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.exception.ParameterException;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.pojo.Feeds;
import com.imooc.commons.model.vo.FeedsVO;
import com.imooc.commons.model.vo.ShortDinerInfo;
import com.imooc.commons.model.vo.SignInDinerInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.feeds.mapper.FeedsMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class FeedsService {

	@Value("${service.name.foodie-oauth-server}")
	private String oauthServerName;
	@Value("${service.name.foodie-follow-server}")
	private String followServerName;
	@Value("${service.name.foodie-diners-server}")
	private String dinersServerName;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private FeedsMapper feedsMapper;
	@Resource
	private RedisTemplate redisTemplate;

	/**
	 * 根据时间由近至远，每次查询 20 条 Feed
	 *
	 * @param page
	 * @param accessToken
	 * @return
	 */
	public List<FeedsVO> selectForPage(Integer page, final String accessToken) {
		if (page == null) {
			page = 1;
		}
		// 获取登录用户
		final SignInDinerInfo dinerInfo = this.loadSignInDinerInfo(accessToken);
		// 我关注的好友的 Feedkey
		final String key = RedisKeyConstant.following_feeds.getKey() + dinerInfo.getId();
		// SortedSet 的 ZREVRANGE 命令是闭区间
		final long start = (page - 1) * ApiConstant.PAGE_SIZE;
		final long end = page * ApiConstant.PAGE_SIZE - 1;
		final Set<Integer> feedIds = this.redisTemplate.opsForZSet().reverseRange(key, start, end);
		if (feedIds == null || feedIds.isEmpty()) {
			return Lists.newArrayList();
		}
		// 根据多主键查询 Feed
		final List<Feeds> feeds = this.feedsMapper.findFeedsByIds(feedIds);
		// 初始化关注好友 ID 集合
		final List<Integer> followingDinerIds = new ArrayList<>();
		// 添加用户 ID 至集合，顺带将 Feeds 转为 Vo 对象
		final List<FeedsVO> feedsVOS = feeds.stream().map(feed -> {
			final FeedsVO feedsVO = new FeedsVO();
			BeanUtil.copyProperties(feed, feedsVO);
			// 添加用户 ID
			followingDinerIds.add(feed.getFkDinerId());
			return feedsVO;
		}).collect(Collectors.toList());
		// 远程调用获取 Feed 中用户信息
		final ResultInfo resultInfo = this.restTemplate.getForObject(this.dinersServerName + "findByIds?access_token=${accessToken}&ids={ids}",
			ResultInfo.class, accessToken, followingDinerIds);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new ParameterException(resultInfo.getCode(), resultInfo.getMessage());
		}
		final List<LinkedHashMap> dinerInfoMaps = (ArrayList) resultInfo.getData();
		// 构建一个 key 为用户 ID，value 为 ShortDinerInfo 的 Map
		final Map<Integer, ShortDinerInfo> dinerInfos = dinerInfoMaps.stream()
			.collect(Collectors.toMap(
				// key
				diner -> (Integer) diner.get("id"),
				// value
				diner -> BeanUtil.fillBeanWithMap(diner, new ShortDinerInfo(), true)
			));
		// 循环 VO 集合，根据用户 ID 从 Map 中获取用户信息并设置至 VO 对象
		feedsVOS.forEach(feedsVO -> {
			feedsVO.setDinerInfo(dinerInfos.get(feedsVO.getFkDinerId()));
		});
		return feedsVOS;
	}

	/**
	 * 变更 Feed
	 *
	 * @param followingDinerId 关注的好友 ID
	 * @param accessToken      登录用户token
	 * @param type             1 关注 0 取关
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addFollowingFeed(final Integer followingDinerId, final String accessToken, final int type) {
		// 请选择关注的好友
		AssertUtil.isTrue(followingDinerId == null || followingDinerId < 1,
			"请选择关注的好友");
		// 获取登录用户信息
		final SignInDinerInfo dinerInfo = this.loadSignInDinerInfo(accessToken);
		// 获取关注/取关的食客的所有 Feed
		final List<Feeds> feedsList = this.feedsMapper.findByDinerId(followingDinerId);
		final String key = RedisKeyConstant.following_feeds.getKey() + dinerInfo.getId();
		if (type == 0) {
			// 取关
			final List<Integer> feedIds = feedsList.stream()
				.map(feed -> feed.getId())
				.collect(Collectors.toList());
            this.redisTemplate.opsForZSet().remove(key, feedIds.toArray(new Integer[]{}));
		} else {
			// 关注
			final Set<ZSetOperations.TypedTuple> typedTuples =
				feedsList.stream()
					.map(feed -> new DefaultTypedTuple<>(feed.getId(), (double) feed.getUpdateDate().getTime()))
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
	public void delete(final Integer id, final String accessToken) {
		// 请选择要删除的 Feed
		AssertUtil.isTrue(id == null || id < 1, "请选择要删除的Feed");
		// 获取登录用户
		final SignInDinerInfo dinerInfo = this.loadSignInDinerInfo(accessToken);
		// 获取 Feed 内容
		final Feeds feeds = this.feedsMapper.findById(id);
		// 判断 Feed 是否已经被删除且只能删除自己的 Feed
		AssertUtil.isTrue(feeds == null, "该Feed已被删除");
		AssertUtil.isTrue(!feeds.getFkDinerId().equals(dinerInfo.getId()),
			"只能删除自己的Feed");
		// 删除
		final int count = this.feedsMapper.delete(id);
		if (count == 0) {
			return;
		}
		// 将内容从粉丝的集合中删除 -- 异步消息队列优化
		// 先获取我的粉丝
		final List<Integer> followers = this.findFollowers(dinerInfo.getId());
		// 移除 Feed
		followers.forEach(follower -> {
			final String key = RedisKeyConstant.following_feeds.getKey() + follower;
            this.redisTemplate.opsForZSet().remove(key, feeds.getId());
		});
	}

	/**
	 * 添加 Feed
	 *
	 * @param feeds
	 * @param accessToken
	 */
	@Transactional(rollbackFor = Exception.class)
	public void create(final Feeds feeds, final String accessToken) {
		// 校验 Feed 内容不能为空，不能太长
		AssertUtil.isNotEmpty(feeds.getContent(), "请输入内容");
		AssertUtil.isTrue(feeds.getContent().length() > 255, "输入内容太多，请重新输入");
		// 获取登录用户信息
		final SignInDinerInfo dinerInfo = this.loadSignInDinerInfo(accessToken);
		// Feed 关联用户信息
		feeds.setFkDinerId(dinerInfo.getId());
		// 添加 Feed
		final int count = this.feedsMapper.save(feeds);
		AssertUtil.isTrue(count == 0, "添加失败");
		// 推送到粉丝的列表中 -- 后续这里应该采用异步消息队列解决性能问题
		// 先获取粉丝 id 集合
		final List<Integer> followers = this.findFollowers(dinerInfo.getId());
		// 推送 Feed
		final long now = System.currentTimeMillis();
		followers.forEach(follower -> {
			final String key = RedisKeyConstant.following_feeds.getKey() + follower;
            this.redisTemplate.opsForZSet().add(key, feeds.getId(), now);
		});
	}

	/**
	 * 获取粉丝 id 集合
	 *
	 * @param dinerId
	 * @return
	 */
	private List<Integer> findFollowers(final Integer dinerId) {
		final String url = this.followServerName + "followers/" + dinerId;
		final ResultInfo resultInfo = this.restTemplate.getForObject(url, ResultInfo.class);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new ParameterException(resultInfo.getCode(), resultInfo.getMessage());
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
	private SignInDinerInfo loadSignInDinerInfo(final String accessToken) {
		// 必须登录
		AssertUtil.mustLogin(accessToken);
		final String url = this.oauthServerName + "user/me?access_token={accessToken}";
		final ResultInfo resultInfo = this.restTemplate.getForObject(url, ResultInfo.class, accessToken);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new ParameterException(resultInfo.getCode(), resultInfo.getMessage());
		}
		final SignInDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(),
			new SignInDinerInfo(), false);
		return dinerInfo;
	}

}
