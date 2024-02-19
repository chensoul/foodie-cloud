package com.chensoul.domain.point.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chensoul.client.UserClient;
import com.chensoul.constant.RedisKeyConstant;
import com.chensoul.core.constant.Constant;
import com.chensoul.core.model.R;
import com.chensoul.domain.point.entity.Point;
import com.chensoul.domain.point.mapper.PointMapper;
import com.chensoul.domain.point.model.UserPointRankVO;
import com.chensoul.domain.user.entity.User;
import com.chensoul.domain.user.model.SimpleUser;
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

/**
 * 积分业务逻辑层
 */
@Service
@AllArgsConstructor
public class PointService extends ServiceImpl<PointMapper, Point> implements IService<Point> {
	private RedisTemplate redisTemplate;
	private UserClient userClient;
	private static final int TOPN = 20;

	/**
	 * 添加积分
	 */
	@Transactional(rollbackFor = Exception.class)
	public Point addPoint(final Point point) {
		this.baseMapper.insert(point);

		this.redisTemplate.opsForZSet()
			.incrementScore(RedisKeyConstant.POINT.getKey(), point.getUserId(), point.getScore());

		return point;
	}

	/**
	 * 查询前 20 积分排行榜，并显示个人排名 -- Redis
	 *
	 * @return
	 */
	public List<UserPointRankVO> listPointRankFromRedis() {
		// 获取登录用户信息
		final User loggedUser = this.userClient.getCurrentUser().getData();
		final Set<ZSetOperations.TypedTuple<Long>> rangeWithScores = this.redisTemplate.opsForZSet()
			.reverseRangeWithScores(RedisKeyConstant.POINT.getKey(), 0, 19);
		if (rangeWithScores == null || rangeWithScores.isEmpty()) {
			return Lists.newArrayList();
		}

		final Set<Long> rankUserIds = Sets.newHashSet();
		final Map<Long, UserPointRankVO> ranksMap = new LinkedHashMap<>();
		int rank = 1;
		for (final ZSetOperations.TypedTuple<Long> rangeWithScore : rangeWithScores) {
			final Long userId = rangeWithScore.getValue();
			final int point = rangeWithScore.getScore().intValue();
			rankUserIds.add(userId);
			final UserPointRankVO userPointRankVO = new UserPointRankVO();
			userPointRankVO.setId(userId);
			userPointRankVO.setRank(rank);
			userPointRankVO.setTotal(point);
			ranksMap.put(userId, userPointRankVO);
			rank++;
		}

		final R<List<User>> resultInfo = this.userClient.list(rankUserIds);
		if (resultInfo.getCode() != Constant.SUCCESS_CODE) {
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
		if (ranksMap.containsKey(loggedUser.getId())) {
			final UserPointRankVO rankVO = ranksMap.get(loggedUser.getId());
			rankVO.setIsMe(1);
			return Lists.newArrayList(ranksMap.values());
		}

		// 如果不在 ranks 中，获取个人排名追加在最后
		final Long myRank = this.redisTemplate.opsForZSet().reverseRank(
			RedisKeyConstant.POINT.getKey(), loggedUser.getId());
		if (myRank != null) {
			final UserPointRankVO me = new UserPointRankVO();
			BeanUtils.copyProperties(loggedUser, me);
			me.setRank(myRank.intValue() + 1);// 排名从 0 开始
			me.setIsMe(1);
			// 获取积分
			final Double point = this.redisTemplate.opsForZSet().score(RedisKeyConstant.POINT.getKey(),
				loggedUser.getId());
			me.setTotal(point.intValue());
			ranksMap.put(loggedUser.getId(), me);
		}
		return Lists.newArrayList(ranksMap.values());
	}

	public List<UserPointRankVO> listPointRank() {
		final User loggedUser = this.userClient.getCurrentUser().getData();
		final List<UserPointRankVO> ranks = this.baseMapper.findTopN(TOPN);
		if (ranks == null || ranks.isEmpty()){
			return Lists.newArrayList();
		}
		final Map<Long, UserPointRankVO> ranksMap = new LinkedHashMap<>();
		for (int i = 0; i < ranks.size(); i++) ranksMap.put(ranks.get(i).getId(), ranks.get(i));
		// 判断个人是否在 ranks 中，如果在，添加标记直接返回
		if (ranksMap.containsKey(loggedUser.getId())) {
			final UserPointRankVO myRank = ranksMap.get(loggedUser.getId());
			myRank.setIsMe(1);
			return Lists.newArrayList(ranksMap.values());
		}
		// 如果不在 ranks 中，获取个人排名追加在最后
		final UserPointRankVO myRank = this.baseMapper.findUserRank(loggedUser.getId());
		myRank.setIsMe(1);
		ranks.add(myRank);
		return ranks;
	}
}
