package com.imooc.points.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.exception.ParameterException;
import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.pojo.DinerPoints;
import com.imooc.commons.model.vo.DinerPointsRankVO;
import com.imooc.commons.model.vo.ShortDinerInfo;
import com.imooc.commons.model.vo.SignInDinerInfo;
import com.imooc.commons.utils.AssertUtil;
import com.imooc.points.mapper.DinerPointsMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * 积分业务逻辑层
 */
@Service
public class DinerPointsService {

	@Resource
	private DinerPointsMapper dinerPointsMapper;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private RedisTemplate redisTemplate;
	@Value("${service.name.foodie-oauth-server}")
	private String oauthServerName;
	@Value("${service.name.foodie-diners-server}")
	private String dinersServerName;
	// 排行榜 TOPN
	private static final int TOPN = 20;

	/**
	 * 添加积分
	 *
	 * @param dinerId 食客ID
	 * @param points  积分
	 * @param types   类型 0=签到，1=关注好友，2=添加Feed，3=添加商户评论
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addPoints(final Integer dinerId, final Integer points, final Integer types) {
		// 基本参数校验
		AssertUtil.isTrue(dinerId == null || dinerId < 1, "食客不能为空");
		AssertUtil.isTrue(points == null || points < 1, "积分不能为空");
		AssertUtil.isTrue(types == null, "请选择对应的积分类型");

		// 插入数据库
		final DinerPoints dinerPoints = new DinerPoints();
		dinerPoints.setFkDinerId(dinerId);
		dinerPoints.setPoints(points);
		dinerPoints.setTypes(types);
        this.dinerPointsMapper.save(dinerPoints);

		// 将积分保存到 Redis
        this.redisTemplate.opsForZSet().incrementScore(
			RedisKeyConstant.diner_points.getKey(), dinerId, points);
	}

	/**
	 * 查询前 20 积分排行榜，并显示个人排名 -- Redis
	 *
	 * @param accessToken
	 * @return
	 */
	public List<DinerPointsRankVO> findDinerPointRankFromRedis(final String accessToken) {
		// 获取登录用户信息
		final SignInDinerInfo signInDinerInfo = this.loadSignInDinerInfo(accessToken);
		// 统计积分排行榜
		final Set<ZSetOperations.TypedTuple<Integer>> rangeWithScores = this.redisTemplate.opsForZSet().reverseRangeWithScores(
			RedisKeyConstant.diner_points.getKey(), 0, 19);
		if (rangeWithScores == null || rangeWithScores.isEmpty()) {
			return Lists.newArrayList();
		}
		// 初始化食客 ID 集合
		final List<Integer> rankDinerIds = Lists.newArrayList();
		// 根据 key：食客 ID value：积分信息 构建一个 Map
		final Map<Integer, DinerPointsRankVO> ranksMap = new LinkedHashMap<>();
		// 初始化排名
		int rank = 1;
		// 循环处理排行榜，添加排名信息
		for (final ZSetOperations.TypedTuple<Integer> rangeWithScore : rangeWithScores) {
			// 食客ID
			final Integer dinerId = rangeWithScore.getValue();
			// 积分
			final int points = rangeWithScore.getScore().intValue();
			// 将食客 ID 添加至食客 ID 集合
			rankDinerIds.add(dinerId);
			final DinerPointsRankVO dinerPointsRankVO = new DinerPointsRankVO();
			dinerPointsRankVO.setId(dinerId);
			dinerPointsRankVO.setRanks(rank);
			dinerPointsRankVO.setTotal(points);
			// 将 VO 对象添加至 Map 中
			ranksMap.put(dinerId, dinerPointsRankVO);
			// 排名 +1
			rank++;
		}

		// 获取 Diners 用户信息
		final ResultInfo resultInfo = this.restTemplate.getForObject(this.dinersServerName +
                                                                     "findByIds?access_token=${accessToken}&ids={ids}",
			ResultInfo.class, accessToken, StrUtil.join(",", rankDinerIds));
		if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
			throw new ParameterException(resultInfo.getCode(), resultInfo.getMessage());
		}
		final List<LinkedHashMap> dinerInfoMaps = (List<LinkedHashMap>) resultInfo.getData();
		// 完善食客昵称和头像
		for (final LinkedHashMap dinerInfoMap : dinerInfoMaps) {
			final ShortDinerInfo shortDinerInfo = BeanUtil.fillBeanWithMap(dinerInfoMap,
				new ShortDinerInfo(), false);
			final DinerPointsRankVO rankVO = ranksMap.get(shortDinerInfo.getId());
			rankVO.setNickname(shortDinerInfo.getNickname());
			rankVO.setAvatarUrl(shortDinerInfo.getAvatarUrl());
		}

		// 判断个人是否在 ranks 中，如果在，添加标记直接返回
		if (ranksMap.containsKey(signInDinerInfo.getId())) {
			final DinerPointsRankVO rankVO = ranksMap.get(signInDinerInfo.getId());
			rankVO.setIsMe(1);
			return Lists.newArrayList(ranksMap.values());
		}

		// 如果不在 ranks 中，获取个人排名追加在最后
		// 获取排名
		final Long myRank = this.redisTemplate.opsForZSet().reverseRank(
			RedisKeyConstant.diner_points.getKey(), signInDinerInfo.getId());
		if (myRank != null) {
			final DinerPointsRankVO me = new DinerPointsRankVO();
			BeanUtils.copyProperties(signInDinerInfo, me);
			me.setRanks(myRank.intValue() + 1);// 排名从 0 开始
			me.setIsMe(1);
			// 获取积分
			final Double points = this.redisTemplate.opsForZSet().score(RedisKeyConstant.diner_points.getKey(),
				signInDinerInfo.getId());
			me.setTotal(points.intValue());
			ranksMap.put(signInDinerInfo.getId(), me);
		}
		return Lists.newArrayList(ranksMap.values());
	}

	/**
	 * 查询前 20 积分排行榜，并显示个人排名 -- MySQL
	 *
	 * @param accessToken
	 * @return
	 */
	public List<DinerPointsRankVO> findDinerPointRank(final String accessToken) {
		// 获取登录用户信息
		final SignInDinerInfo signInDinerInfo = this.loadSignInDinerInfo(accessToken);
		// 统计积分排行榜
		final List<DinerPointsRankVO> ranks = this.dinerPointsMapper.findTopN(TOPN);
		if (ranks == null || ranks.isEmpty()) {
			return Lists.newArrayList();
		}
		// 根据 key：食客 ID value：积分信息 构建一个 Map
		final Map<Integer, DinerPointsRankVO> ranksMap = new LinkedHashMap<>();
		for (int i = 0; i < ranks.size(); i++) {
			ranksMap.put(ranks.get(i).getId(), ranks.get(i));
		}
		// 判断个人是否在 ranks 中，如果在，添加标记直接返回
		if (ranksMap.containsKey(signInDinerInfo.getId())) {
			final DinerPointsRankVO myRank = ranksMap.get(signInDinerInfo.getId());
			myRank.setIsMe(1);
			return Lists.newArrayList(ranksMap.values());
		}
		// 如果不在 ranks 中，获取个人排名追加在最后
		final DinerPointsRankVO myRank = this.dinerPointsMapper.findDinerRank(signInDinerInfo.getId());
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
		if (dinerInfo == null) {
			throw new ParameterException(ApiConstant.NO_LOGIN_CODE, ApiConstant.NO_LOGIN_MESSAGE);
		}
		return dinerInfo;
	}

}
