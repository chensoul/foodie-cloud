package com.imooc.follow.service;

import com.imooc.auth.entity.User;
import com.imooc.auth.model.SimpleUser;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.Follow;
import com.imooc.follow.mapper.FollowMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 关注/取关业务逻辑层
 */
@Service
public class FollowService {

	@Value("${service.name.foodie-auth}")
	private String oauthServerName;
	@Value("${service.name.foodie-user}")
	private String usersServerName;
	@Value("${service.name.foodie-feed}")
	private String feedsServerName;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private FollowMapper followMapper;
	@Resource
	private RedisTemplate redisTemplate;

	/**
	 * 获取粉丝列表
	 *
	 * @param userId
	 * @return
	 */
	public Set<Integer> findFollower(final Long userId) {
		Assert.isNull(userId, "请选择要查看的用户");
		final Set<Integer> followers = this.redisTemplate.opsForSet()
			.members(RedisKeyConstant.FOLLOWER.getKey() + userId);
		return followers;
	}

	/**
	 * 共同关注列表
	 *
	 * @param userId
	 * @param accessToken
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public List<SimpleUser> findCommonsFriend(final Integer userId, final String accessToken) {
		// 是否选择了查看对象
		Assert.isTrue(userId == null || userId < 1,
			"请选择要查看的人");
		// 获取登录用户信息
		final User userInfo = this.loadSignInUserInfo(accessToken);
		// 获取登录用户的关注信息
		final String loginUserKey = RedisKeyConstant.FOLLOWING.getKey() + userInfo.getId();
		// 获取登录用户查看对象的关注信息
		final String userKey = RedisKeyConstant.FOLLOWING.getKey() + userId;
		// 计算交集
		final Set<Integer> userIds = this.redisTemplate.opsForSet().intersect(loginUserKey, userKey);
		// 没有
		if (userIds == null || userIds.isEmpty()) {
			return new ArrayList<>();
		}
		// 调用食客服务根据 ids 查询食客信息
		final R resultInfo = this.restTemplate.getForObject(this.usersServerName + "findByIds?access_token={accessToken}&ids={ids}",
			R.class, accessToken, StringUtils.join(",", userIds));
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		// 处理结果集
		final List<LinkedHashMap> dinnerInfoMaps = (ArrayList) resultInfo.getData();
		final List<SimpleUser> userInfos = dinnerInfoMaps.stream()
			.map(user -> {
				final SimpleUser shortUserInfo = new SimpleUser();
				BeanUtils.copyProperties(user, shortUserInfo);
				return shortUserInfo;
			})
			.collect(Collectors.toList());
		return userInfos;
	}

	public void follow(final Long followUserId, final int isFoolowed,
					   final String accessToken) {
		// 是否选择了关注对象
		Assert.isTrue(followUserId == null || followUserId < 1,
			"请选择要关注的人");
		// 获取登录用户信息 (封装方法)
		final User userInfo = this.loadSignInUserInfo(accessToken);
		// 获取当前登录用户与需要关注用户的关注信息
		final Follow follow = this.followMapper.selectFollow(userInfo.getId(), followUserId);

		// 如果没有关注信息，且要进行关注操作 -- 添加关注
		if (follow == null && isFoolowed == 1) {
			// 添加关注信息
			final int count = this.followMapper.save(userInfo.getId(), followUserId);
			// 添加关注列表到 Redis
			if (count == 1) {
				this.addToRedisSet(userInfo.getId(), followUserId);
				// 保存 Feed
				this.sendSaveOrRemoveFeed(followUserId, accessToken, 1);
			}
		}

		// 如果有关注信息，且目前处于关注状态，且要进行取关操作 -- 取关关注
		if (follow != null && isFoolowed == 0) {
			// 取关
			final int count = this.followMapper.update(follow.getId(), isFoolowed);
			// 移除 Redis 关注列表
			if (count == 1) {
				this.removeFromRedisSet(userInfo.getId(), followUserId);
				// 移除 Feed
				this.sendSaveOrRemoveFeed(followUserId, accessToken, 0);
			}
		}
		// 如果有关注信息，且目前处于取关状态，且要进行关注操作 -- 重新关注
		if (follow != null && isFoolowed == 1) {
			// 重新关注
			final int count = this.followMapper.update(follow.getId(), isFoolowed);
			// 添加关注列表到 Redis
			if (count == 1) {
				this.addToRedisSet(userInfo.getId(), followUserId);
				// 添加 Feed
				this.sendSaveOrRemoveFeed(followUserId, accessToken, 1);
			}
		}
	}

	/**
	 * 发送请求添加或者移除关注人的Feed列表
	 *
	 * @param followUserId 关注好友的ID
	 * @param accessToken  当前登录用户token
	 * @param type         0=取关 1=关注
	 */
	private void sendSaveOrRemoveFeed(final Long followUserId, final String accessToken, final int type) {
		final String feedsUpdateUrl = this.feedsServerName + "updateFollowingFeeds/"
									  + followUserId + "?access_token=" + accessToken;
		// 构建请求头
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// 构建请求体（请求参数）
		final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("type", type);
		final HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
		this.restTemplate.postForEntity(feedsUpdateUrl, entity, R.class);
	}

	/**
	 * 添加关注列表到 Redis
	 *
	 * @param userId
	 * @param followUserId
	 */
	private void addToRedisSet(final Long userId, final Long followUserId) {
		this.redisTemplate.opsForSet().add(RedisKeyConstant.FOLLOWING.getKey() + userId, followUserId);
		this.redisTemplate.opsForSet().add(RedisKeyConstant.FOLLOWER.getKey() + followUserId, userId);
	}

	/**
	 * 移除 Redis 关注列表
	 *
	 * @param userId
	 * @param followUserId
	 */
	private void removeFromRedisSet(final Long userId, final Long followUserId) {
		this.redisTemplate.opsForSet().remove(RedisKeyConstant.FOLLOWING.getKey() + userId, followUserId);
		this.redisTemplate.opsForSet().remove(RedisKeyConstant.FOLLOWER.getKey() + followUserId, userId);
	}

	/**
	 * 获取登录用户信息
	 *
	 * @param accessToken
	 * @return
	 */
	private User loadSignInUserInfo(final String accessToken) {
		final String url = this.oauthServerName + "diner/me?access_token={accessToken}";
		final R resultInfo = this.restTemplate.getForObject(url, R.class, accessToken);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		return null;
	}
}
