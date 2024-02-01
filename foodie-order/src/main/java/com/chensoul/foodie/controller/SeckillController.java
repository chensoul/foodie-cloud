package com.chensoul.foodie.controller;

import com.chensoul.core.model.R;
import com.chensoul.foodie.domain.order.entity.SeckillVoucher;
import com.chensoul.foodie.domain.order.service.SeckillService;
import com.fasterxml.jackson.core.JsonProcessingException;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀控制层
 */
@RestController
@AllArgsConstructor
public class SeckillController {

	private SeckillService seckillService;

	/**
	 * 秒杀下单
	 *
	 * @param voucherId
	 * @return
	 */
	@PostMapping("{voucherId}")
	public R<Void> doSeckill(@PathVariable @NotNull(message = "请选择需要抢购的代金券") final Integer voucherId) throws JsonProcessingException {
		seckillService.doSeckill(voucherId);
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
		seckillService.addSeckillVouchers(seckillVoucher);
		return R.ok();
	}

}
