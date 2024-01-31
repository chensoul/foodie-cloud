package com.chensoul.order.controller;

import com.chensoul.commons.model.domain.R;
import com.chensoul.commons.model.entity.SeckillVoucher;
import com.chensoul.order.service.SeckillService;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
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
	 * @return
	 */
	@PostMapping("{voucherId}")
	public R<Void> doSeckill(@PathVariable @NotNull(message = "请选择需要抢购的代金券") final Integer voucherId) throws JsonProcessingException {
		this.seckillService.doSeckill(voucherId);
		return R.ok();
	}

	/**
	 * 新增秒杀活动
	 *
	 * @param seckillVoucher
	 * @return
	 */
	@PostMapping("add")
	public R<Void> addSeckillVouchers(@RequestBody final SeckillVoucher seckillVoucher) {
		this.seckillService.addSeckillVouchers(seckillVoucher);
		return R.ok();
	}

}
