package com.imooc.diner.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.constant.RedisKeyConstant;
import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.Diner;
import com.imooc.commons.model.vo.NearMeDinerVO;
import com.imooc.commons.model.vo.ShortDinerInfo;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
public class NearMeService {

	@Resource
	private DinerService dinerService;
	@Value("${service.name.foodie-oauth-server}")
	private String oauthServerName;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private RedisTemplate redisTemplate;

	/**
	 * 更新食客坐标
	 *
	 * @param accessToken 登录用户 token
	 * @param lon         经度
	 * @param lat         纬度
	 */
	public void updateDinerLocation(final String accessToken, final Float lon, final Float lat) {
		// 参数校验
		Assert.isTrue(lon != null, "获取经度失败");
		Assert.isTrue(lat != null, "获取纬度失败");
		// 获取登录用户信息
		final Diner signInDinerInfo = this.loadSignInDinerInfo(accessToken);
		// 获取 key diner:location
		final String key = RedisKeyConstant.diner_location.getKey();
		// 将用户地理位置信息存入 Redis
		final RedisGeoCommands.GeoLocation geoLocation = new RedisGeoCommands
			.GeoLocation(signInDinerInfo.getId(), new Point(lon, lat));
		this.redisTemplate.opsForGeo().add(key, geoLocation);
	}

	/**
	 * 获取附近的人
	 *
	 * @param accessToken 用户登录 token
	 * @param radius      半径，默认 1000m
	 * @param lon         经度
	 * @param lat         纬度
	 * @return
	 */
	public List<NearMeDinerVO> findNearMe(final String accessToken,
										  Integer radius,
										  final Float lon, final Float lat) {
		// 获取登录用户信息
		final Diner signInDinerInfo = this.loadSignInDinerInfo(accessToken);
		// 食客 ID
		final Long dinerId = signInDinerInfo.getId();
		// 处理半径，默认 1000m
		if (radius == null) {
			radius = 1000;
		}
		// 获取 key
		final String key = RedisKeyConstant.diner_location.getKey();
		// 获取用户经纬度
		Point point = null;
		if (lon == null || lat == null) {
			// 如果经纬度没传，那么从 Redis 中获取
			final List<Point> points = this.redisTemplate.opsForGeo().position(key, dinerId);
			Assert.isTrue(points != null && !points.isEmpty(),
				"获取经纬度失败");
			point = points.get(0);
		} else {
			point = new Point(lon, lat);
		}
		// 初始化距离对象，单位 m
		final Distance distance = new Distance(radius,
			RedisGeoCommands.DistanceUnit.METERS);
		// 初始化 Geo 命令参数对象
		final RedisGeoCommands.GeoRadiusCommandArgs args =
			RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
		// 附近的人限制 20，包含距离，按由近到远排序
		args.limit(20).includeDistance().sortAscending();
		// 以用户经纬度为圆心，范围 1000m
		final Circle circle = new Circle(point, distance);
		// 获取附近的人 GeoLocation 信息
		final GeoResults<RedisGeoCommands.GeoLocation> geoResult =
			this.redisTemplate.opsForGeo().radius(key, circle, args);
		// 构建有序 Map
		final Map<Long, NearMeDinerVO> nearMeDinerVOMap = Maps.newLinkedHashMap();
		// 完善用户头像昵称信息
		geoResult.forEach(result -> {
			final RedisGeoCommands.GeoLocation<Long> geoLocation = result.getContent();
			// 初始化 Vo 对象
			final NearMeDinerVO nearMeDinerVO = new NearMeDinerVO();
			nearMeDinerVO.setId(geoLocation.getName());
			// 获取距离
			final Double dist = result.getDistance().getValue();
			// 四舍五入精确到小数点后 1 位，方便客户端显示
			final String distanceStr = dist.toString() + "m";
			nearMeDinerVO.setDistance(distanceStr);
			nearMeDinerVOMap.put(geoLocation.getName(), nearMeDinerVO);
		});
		// 获取附近的人的信息（根据 Diner 服务接口获取）
		final Integer[] dinerIds = nearMeDinerVOMap.keySet().toArray(new Integer[]{});
		final List<ShortDinerInfo> shortDinerInfos = this.dinerService.findByIds(StringUtils.join(",", dinerIds));
		// 完善昵称头像信息
		shortDinerInfos.forEach(shortDinerInfo -> {
			final NearMeDinerVO nearMeDinerVO = nearMeDinerVOMap.get(shortDinerInfo.getId());
			nearMeDinerVO.setNickname(shortDinerInfo.getNickname());
			nearMeDinerVO.setAvatarUrl(shortDinerInfo.getAvatarUrl());
		});
		return Lists.newArrayList(nearMeDinerVOMap.values());
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
