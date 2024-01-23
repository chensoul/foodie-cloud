package com.imooc.points.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.vo.DinerPointsRankVO;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.points.service.DinerPointsService;
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
public class DinerPointsController {

	@Resource
	private DinerPointsService dinerPointsService;

	/**
	 * 添加积分
	 *
	 * @param dinerId 食客ID
	 * @param points  积分
	 * @param types   类型 0=签到，1=关注好友，2=添加Feed，3=添加商户评论
	 * @return
	 */
	@PostMapping
	public ResultInfo<Integer> addPoints(@RequestParam(required = false) final Integer dinerId,
										 @RequestParam(required = false) final Integer points,
										 @RequestParam(required = false) final Integer types) {
        this.dinerPointsService.addPoints(dinerId, points, types);
		return ResultInfoUtil.buildSuccess(points);
	}

	/**
	 * 查询前 20 积分排行榜，同时显示用户排名 -- Redis
	 *
	 * @param access_token
	 * @return
	 */
	@GetMapping("redis")
	public ResultInfo findDinerPointsRankFromRedis(final String access_token) {
		final List<DinerPointsRankVO> ranks = this.dinerPointsService.findDinerPointRankFromRedis(access_token);
		return ResultInfoUtil.buildSuccess(ranks);
	}

	/**
	 * 查询前 20 积分排行榜，同时显示用户排名 -- MySQL
	 *
	 * @param access_token
	 * @return
	 */
	@GetMapping
	public ResultInfo findDinerPointsRank(final String access_token) {
		final List<DinerPointsRankVO> ranks = this.dinerPointsService.findDinerPointRank(access_token);
		return ResultInfoUtil.buildSuccess(ranks);
	}

}
