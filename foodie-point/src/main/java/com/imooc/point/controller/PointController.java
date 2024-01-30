package com.imooc.point.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.point.model.UserPointRankVO;
import com.imooc.point.service.PointService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分控制层
 */
@RestController
public class PointController {

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
	@PostMapping
	public R<Void> addPoint(@RequestParam(required = false) final Long userId,
							@RequestParam(required = false) final Integer point,
							@RequestParam(required = false) final Integer type) {
		this.pointService.addPoint(userId, point, type);
		return R.ok();
	}

	/**
	 * 查询前 20 积分排行榜，同时显示用户排名 -- Redis
	 *
	 * @param access_token
	 * @return
	 */
	@GetMapping("redis")
	public R findUserPointRankFromRedis(final String access_token) {
		final List<UserPointRankVO> ranks = this.pointService.findUserPointRankFromRedis(access_token);
		return R.ok(ranks);
	}

	/**
	 * 查询前 20 积分排行榜，同时显示用户排名 -- MySQL
	 *
	 * @param access_token
	 * @return
	 */
	@GetMapping
	public R findUserPointRank(final String access_token) {
		final List<UserPointRankVO> ranks = this.pointService.findUserPointRank(access_token);
		return R.ok(ranks);
	}

}
