package com.chensoul.domain.follow.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chensoul.client.UserClient;
import com.chensoul.constant.RedisKeyConstant;
import com.chensoul.core.constant.Constant;
import com.chensoul.core.model.R;
import com.chensoul.domain.entity.Follow;
import com.chensoul.domain.follow.mapper.FollowMapper;
import com.chensoul.domain.user.entity.User;
import com.chensoul.domain.user.model.SimpleUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 关注/取关业务逻辑层
 */
@Service
@AllArgsConstructor
public class FollowService extends ServiceImpl<FollowMapper, Follow> implements IService<Follow> {
	private RedisTemplate redisTemplate;
	private UserClient userClient;

	/**
	 * 获取粉丝列表
	 *
	 * @param userId
	 * @return
	 */
	public Set<Integer> findFollower(final Long userId) {
		Assert.isNull(userId, "请选择要查看的用户");
		final Set<Integer> followers = this.redisTemplate.opsForSet().members(RedisKeyConstant.FOLLOWER.getKey() + userId);
		return followers;
	}

	/**
	 * 共同关注列表
	 *
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public List<SimpleUser> findCommonsFriend(final Integer userId) {
		Assert.isTrue(userId == null || userId < 1, "请选择要查看的人");

		final User userInfo = this.loadSignInUserInfo();
		final String loginUserKey = RedisKeyConstant.FOLLOWING.getKey() + userInfo.getId();
		final String userKey = RedisKeyConstant.FOLLOWING.getKey() + userId;
		final Set<Long> userIds = this.redisTemplate.opsForSet().intersect(loginUserKey, userKey);
		if (userIds == null || userIds.isEmpty()) {
			return new ArrayList<>();
		}
		final R<List<User>> resultInfo = this.userClient.list(userIds);
		if (resultInfo.getCode() != Constant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		final List<User> users = resultInfo.getData();
		return users.stream()
			.map(user -> {
				final SimpleUser shortUserInfo = new SimpleUser();
				BeanUtils.copyProperties(user, shortUserInfo);
				return shortUserInfo;
			})
			.collect(Collectors.toList());
	}

	public void follow(final Long followUserId, final int isFollowed) {
		Assert.isTrue(followUserId == null || followUserId < 1, "请选择要关注的人");
		final User userInfo = this.loadSignInUserInfo();
		Follow follow = this.baseMapper.selectOne(Wrappers.<Follow>lambdaQuery().eq(Follow::getUserId, userInfo.getId())
			.eq(Follow::getFollowUserId, followUserId));

		if (follow == null && isFollowed == 1) {
			follow = new Follow();
			follow.setUserId(userInfo.getId());
			follow.setFollowUserId(followUserId);
			final int count = this.baseMapper.insert(follow);
			if (count == 1) {
				this.addToRedisSet(userInfo.getId(), followUserId);
				this.sendSaveOrRemoveFeed(followUserId, 1);
			}
		}

		if (follow != null && isFollowed == 0) {
//			final int count = followMapper.update(follow.getId(), isFollowed);
			this.removeFromRedisSet(userInfo.getId(), followUserId);
			this.sendSaveOrRemoveFeed(followUserId, 0);
		}
		if (follow != null && isFollowed == 1) {
//			final int count = followMapper.update(follow.getId(), isFollowed);
			this.addToRedisSet(userInfo.getId(), followUserId);
			this.sendSaveOrRemoveFeed(followUserId, 1);
		}
	}

	/**
	 * 发送请求添加或者移除关注人的Feed列表
	 *
	 * @param followUserId 关注好友的ID
	 * @param type         0=取关 1=关注
	 */
	private void sendSaveOrRemoveFeed(final Long followUserId, final int type) {

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
	 * @return
	 */
	private User loadSignInUserInfo() {
		return this.userClient.getCurrentUser().getData();
	}
}
