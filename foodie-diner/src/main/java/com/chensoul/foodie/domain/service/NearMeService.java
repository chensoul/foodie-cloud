package com.chensoul.foodie.domain.service;

import com.chensoul.core.model.R;
import com.chensoul.foodie.client.UserClient;
import com.chensoul.foodie.constant.RedisKeyConstant;
import com.chensoul.foodie.domain.model.NearMeUserVO;
import com.chensoul.foodie.domain.user.entity.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@AllArgsConstructor
public class NearMeService {
	private UserClient userClient;
	private RedisTemplate redisTemplate;

	/**
	 * 更新食客坐标
	 *
	 * @param lon 经度
	 * @param lat 纬度
	 */
	public void updateUserLocation(final Float lon, final Float lat) {
		Assert.isTrue(lon != null, "获取经度失败");
		Assert.isTrue(lat != null, "获取纬度失败");

		final RedisGeoCommands.GeoLocation geoLocation = new RedisGeoCommands
			.GeoLocation(SecurityContextHolder.getContext().getAuthentication().getName(), new Point(lon, lat));
		redisTemplate.opsForGeo().add(RedisKeyConstant.USER_LOCATION.getKey(), geoLocation);
	}

	/**
	 * 获取附近的人
	 *
	 * @param radius 半径，默认 1000m
	 * @param lon    经度
	 * @param lat    纬度
	 * @return
	 */
	public List<NearMeUserVO> findNearMe(Integer radius, final Float lon, final Float lat) {
		if (radius == null) {
			radius = 1000;
		}

		final String key = RedisKeyConstant.USER_LOCATION.getKey();
		Point point = null;
		if (lon == null || lat == null) {
			final List<Point> points = redisTemplate.opsForGeo().position(key, SecurityContextHolder.getContext().getAuthentication().getName());
			Assert.isTrue(points != null && !points.isEmpty(), "获取经纬度失败");
			point = points.get(0);
		} else {
			point = new Point(lon, lat);
		}
		final Distance distance = new Distance(radius, RedisGeoCommands.DistanceUnit.METERS);
		final RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
		args.limit(20).includeDistance().sortAscending();
		final Circle circle = new Circle(point, distance);
		final GeoResults<RedisGeoCommands.GeoLocation> geoResult =
			redisTemplate.opsForGeo().radius(key, circle, args);
		if (geoResult == null) {
			return new ArrayList<>();
		}

		final Map<Long, NearMeUserVO> nearMeUserVOMap = Maps.newLinkedHashMap();
		geoResult.forEach(result -> {
			final RedisGeoCommands.GeoLocation<Long> geoLocation = result.getContent();
			final NearMeUserVO nearMeUserVO = new NearMeUserVO();
			nearMeUserVO.setId(geoLocation.getName());
			final Double dist = result.getDistance().getValue();
			final String distanceStr = dist.toString() + "m";
			nearMeUserVO.setDistance(distanceStr);
			nearMeUserVOMap.put(geoLocation.getName(), nearMeUserVO);
		});

		if (nearMeUserVOMap.size() == 0) {
			return new ArrayList<>();
		}

		final R<List<User>> users = userClient.list(nearMeUserVOMap.keySet());

		if (users.getData() != null) {
			users.getData().forEach(shortUserInfo -> {
				final NearMeUserVO nearMeUserVO = nearMeUserVOMap.get(shortUserInfo.getId());
				nearMeUserVO.setNickname(shortUserInfo.getNickname());
				nearMeUserVO.setAvatar(shortUserInfo.getAvatar());
			});
		}
		return Lists.newArrayList(nearMeUserVOMap.values());
	}

}
