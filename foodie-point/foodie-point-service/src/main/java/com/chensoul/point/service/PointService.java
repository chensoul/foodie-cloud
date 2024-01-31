package com.chensoul.point.service;

import com.chensoul.auth.client.UserClient;
import com.chensoul.auth.entity.User;
import com.chensoul.auth.model.SimpleUser;
import com.chensoul.commons.constant.ApiConstant;
import com.chensoul.commons.constant.RedisKeyConstant;
import com.chensoul.commons.model.domain.R;
import com.chensoul.point.entity.UserPoint;
import com.chensoul.point.mapper.PointMapper;
import com.chensoul.point.model.UserPointRankVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 积分业务逻辑层
 */
@Service
@AllArgsConstructor
public class PointService {
	private PointMapper pointMapper;
	private RedisTemplate redisTemplate;
	private UserClient userClient;

	// 排行榜 TOPN
	private static final int TOPN = 20;

	/**
	 * 添加积分
	 *
	 * @param userId 食客ID
	 * @param point  积分
	 * @param type   类型 0=签到，1=关注好友，2=添加Feed，3=添加商户评论
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addPoint(final Long userId, final Integer point, final Integer type) {
		// 基本参数校验
		Assert.isTrue(userId == null || userId < 1, "食客不能为空");
		Assert.isTrue(point == null || point < 1, "积分不能为空");
		Assert.isTrue(type == null, "请选择对应的积分类型");

		// 插入数据库
		final UserPoint userpoint = new UserPoint();
		userpoint.setUserId(userId);
		userpoint.setPoint(point);
		userpoint.setType(type);
		this.pointMapper.save(userpoint);

		// 将积分保存到 Redis
		this.redisTemplate.opsForZSet().incrementScore(
			RedisKeyConstant.POINT.getKey(), userId, point);
	}

	/**
	 * 查询前 20 积分排行榜，并显示个人排名 -- Redis
	 *
	 * @return
	 */
	public List<UserPointRankVO> findUserPointRankFromRedis() {
		// 获取登录用户信息
		final User signInUserInfo = loadSignInUserInfo();
		// 统计积分排行榜
		final Set<ZSetOperations.TypedTuple<Long>> rangeWithScores = this.redisTemplate.opsForZSet().reverseRangeWithScores(
			RedisKeyConstant.POINT.getKey(), 0, 19);
		if (rangeWithScores == null || rangeWithScores.isEmpty()) {
			return Lists.newArrayList();
		}
		final Set<Long> rankUserIds = Sets.newHashSet();
		// 根据 key：食客 ID value：积分信息 构建一个 Map
		final Map<Long, UserPointRankVO> ranksMap = new LinkedHashMap<>();
		int rank = 1;
		for (final ZSetOperations.TypedTuple<Long> rangeWithScore : rangeWithScores) {
			final Long userId = rangeWithScore.getValue();
			final int point = rangeWithScore.getScore().intValue();
			rankUserIds.add(userId);
			final UserPointRankVO userPointRankVO = new UserPointRankVO();
			userPointRankVO.setId(userId);
			userPointRankVO.setRanks(rank);
			userPointRankVO.setTotal(point);
			ranksMap.put(userId, userPointRankVO);
			rank++;
		}

		final R<List<User>> resultInfo = userClient.findByIds(rankUserIds);
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		final List<User> users = resultInfo.getData();
		for (final User user : users) {
			final SimpleUser shortUserInfo = new SimpleUser();
			BeanUtils.copyProperties(user, shortUserInfo);
			final UserPointRankVO rankVO = ranksMap.get(shortUserInfo.getId());
			rankVO.setNickname(shortUserInfo.getNickname());
			rankVO.setAvatar(shortUserInfo.getAvatar());
		}

		// 判断个人是否在 ranks 中，如果在，添加标记直接返回
		if (ranksMap.containsKey(signInUserInfo.getId())) {
			final UserPointRankVO rankVO = ranksMap.get(signInUserInfo.getId());
			rankVO.setIsMe(1);
			return Lists.newArrayList(ranksMap.values());
		}

		// 如果不在 ranks 中，获取个人排名追加在最后
		final Long myRank = this.redisTemplate.opsForZSet().reverseRank(
			RedisKeyConstant.POINT.getKey(), signInUserInfo.getId());
		if (myRank != null) {
			final UserPointRankVO me = new UserPointRankVO();
			BeanUtils.copyProperties(signInUserInfo, me);
			me.setRanks(myRank.intValue() + 1);// 排名从 0 开始
			me.setIsMe(1);
			// 获取积分
			final Double point = this.redisTemplate.opsForZSet().score(RedisKeyConstant.POINT.getKey(),
				signInUserInfo.getId());
			me.setTotal(point.intValue());
			ranksMap.put(signInUserInfo.getId(), me);
		}
		return Lists.newArrayList(ranksMap.values());
	}

	/**
	 * 查询前 20 积分排行榜，并显示个人排名 -- MySQL
	 *
	 * @return
	 */
	public List<UserPointRankVO> findUserPointRank() {
		final User signInUserInfo = loadSignInUserInfo();
		final List<UserPointRankVO> ranks = this.pointMapper.findTopN(TOPN);
		if (ranks == null || ranks.isEmpty()) {
			return Lists.newArrayList();
		}
		final Map<Long, UserPointRankVO> ranksMap = new LinkedHashMap<>();
		for (int i = 0; i < ranks.size(); i++) {
			ranksMap.put(ranks.get(i).getId(), ranks.get(i));
		}
		// 判断个人是否在 ranks 中，如果在，添加标记直接返回
		if (ranksMap.containsKey(signInUserInfo.getId())) {
			final UserPointRankVO myRank = ranksMap.get(signInUserInfo.getId());
			myRank.setIsMe(1);
			return Lists.newArrayList(ranksMap.values());
		}
		// 如果不在 ranks 中，获取个人排名追加在最后
		final UserPointRankVO myRank = this.pointMapper.findUserRank(signInUserInfo.getId());
		myRank.setIsMe(1);
		ranks.add(myRank);
		return ranks;
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
