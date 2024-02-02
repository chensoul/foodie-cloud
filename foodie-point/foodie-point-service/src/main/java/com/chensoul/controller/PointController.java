package com.chensoul.controller;

import com.chensoul.client.PointApi;
import com.chensoul.core.model.R;
import com.chensoul.domain.point.entity.Point;
import com.chensoul.domain.point.model.UserPointRankVO;
import com.chensoul.domain.point.service.PointService;
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
		return R.ok(this.pointService.addPoint(point));
	}

	public R<List<UserPointRankVO>> listPointRankFromRedis() {
		return R.ok(this.pointService.listPointRankFromRedis());
	}

	public R listPointRank() {
		return R.ok(this.pointService.listPointRank());
	}

}
