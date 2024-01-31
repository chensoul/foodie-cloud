package com.chensoul.point.controller;

import com.chensoul.commons.model.domain.R;
import com.chensoul.point.client.PointApi;
import com.chensoul.point.model.UserPointRankVO;
import com.chensoul.point.service.PointService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分控制层
 */
@RestController
public class PointController implements PointApi {

	@Resource
	private PointService pointService;

	/**
	 * 添加积分
	 *
	 * @param userId 食客ID
	 * @param point  积分
	 * @param type   类型 0=签到，1=关注好友，2=添加Feed，3=添加商户评论
	 * @return
	 */
	@Override
	public R<Void> addPoint(@RequestParam(required = false) final Long userId,
							@RequestParam(required = false) final Integer point,
							@RequestParam(required = false) final Integer type) {
		this.pointService.addPoint(userId, point, type);
		return R.ok();
	}

	/**
	 * 查询前 20 积分排行榜，同时显示用户排名 -- Redis
	 *
	 * @return
	 */
	public R findUserPointRankFromRedis() {
		final List<UserPointRankVO> ranks = this.pointService.findUserPointRankFromRedis();
		return R.ok(ranks);
	}

	/**
	 * 查询前 20 积分排行榜，同时显示用户排名 -- MySQL
	 *
	 * @return
	 */
	public R findUserPointRank() {
		final List<UserPointRankVO> ranks = this.pointService.findUserPointRank();
		return R.ok(ranks);
	}

}
