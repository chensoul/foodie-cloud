package com.chensoul.foodie.controller;

import com.chensoul.core.model.R;
import com.chensoul.foodie.client.PointApi;
import com.chensoul.foodie.domain.point.entity.Point;
import com.chensoul.foodie.domain.point.model.UserPointRankVO;
import com.chensoul.foodie.domain.point.service.PointService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分控制层
 */
@RestController
@AllArgsConstructor
public class PointController implements PointApi {
	private PointService pointService;

	@Override
	public R<Point> addPoint(final Point point) {
		return R.ok(pointService.addPoint(point));
	}

	public R<List<UserPointRankVO>> listPointRankFromRedis() {
		return R.ok(pointService.listPointRankFromRedis());
	}

	public R listPointRank() {
		return R.ok(pointService.listPointRank());
	}

}
