package com.chensoul.point.controller;

import com.chensoul.commons.model.domain.R;
import com.chensoul.point.client.PointApi;
import com.chensoul.point.domain.entity.Point;
import com.chensoul.point.domain.model.UserPointRankVO;
import com.chensoul.point.domain.service.PointService;
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
