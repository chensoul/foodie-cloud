package com.imooc.seckill.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.pojo.SeckillVouchers;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.seckill.service.SeckillService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀控制层
 */
@RestController
public class SeckillController {

	@Resource
	private SeckillService seckillService;

	/**
	 * 秒杀下单
	 *
	 * @param voucherId
	 * @param access_token
	 * @return
	 */
	@PostMapping("{voucherId}")
	public ResultInfo<String> doSeckill(@PathVariable final Integer voucherId, final String access_token) {
		final ResultInfo resultInfo = this.seckillService.doSeckill(voucherId, access_token);
		return resultInfo;
	}

	/**
	 * 新增秒杀活动
	 *
	 * @param seckillVouchers
	 * @return
	 */
	@PostMapping("add")
	public ResultInfo<String> addSeckillVouchers(@RequestBody final SeckillVouchers seckillVouchers) {
		this.seckillService.addSeckillVouchers(seckillVouchers);
		return ResultInfoUtil.buildSuccess("添加成功");
	}

}
