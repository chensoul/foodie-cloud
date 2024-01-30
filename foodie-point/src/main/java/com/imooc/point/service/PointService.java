package com.imooc.point.service;

import com.google.common.collect.Lists;
import com.imooc.auth.entity.User;
import com.imooc.auth.model.SimpleUser;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.UserPoint;
import com.imooc.point.mapper.PointMapper;
import com.imooc.point.model.UserPointRankVO;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * 积分业务逻辑层
 */
@Service
public class PointService {

	@Resource
	private PointMapper pointMapper;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private RedisTemplate redisTemplate;
	@Value("${service.name.foodie-auth}")
	private String oauthServerName;
	@Value("${service.name.foodie-user}")
	private String usersServerName;
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
	 * @param accessToken
	 * @return
	 */
	public List<UserPointRankVO> findUserPointRankFromRedis(final String accessToken) {
		// 获取登录用户信息
		final User signInUserInfo = this.loadSignInUserInfo(accessToken);
		// 统计积分排行榜
		final Set<ZSetOperations.TypedTuple<Long>> rangeWithScores = this.redisTemplate.opsForZSet().reverseRangeWithScores(
			RedisKeyConstant.POINT.getKey(), 0, 19);
		if (rangeWithScores == null || rangeWithScores.isEmpty()) {
			return Lists.newArrayList();
		}
		final List<Long> rankUserIds = Lists.newArrayList();
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

		final R resultInfo = this.restTemplate.getForObject(this.usersServerName +
															"findByIds?access_token=${accessToken}&ids={ids}",
			R.class, accessToken, StringUtils.join(",", rankUserIds));
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new IllegalArgumentException(resultInfo.getMessage());
		}
		final List<LinkedHashMap> userInfoMaps = (List<LinkedHashMap>) resultInfo.getData();
		for (final LinkedHashMap userInfoMap : userInfoMaps) {
			final SimpleUser shortUserInfo = new SimpleUser();
			BeanUtils.copyProperties(userInfoMap, shortUserInfo); //FXIME
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
	 * @param accessToken
	 * @return
	 */
	public List<UserPointRankVO> findUserPointRank(final String accessToken) {
		// 获取登录用户信息
		final User signInUserInfo = this.loadSignInUserInfo(accessToken);
		// 统计积分排行榜
		final List<UserPointRankVO> ranks = this.pointMapper.findTopN(TOPN);
		if (ranks == null || ranks.isEmpty()) {
			return Lists.newArrayList();
		}
		// 根据 key：食客 ID value：积分信息 构建一个 Map
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
