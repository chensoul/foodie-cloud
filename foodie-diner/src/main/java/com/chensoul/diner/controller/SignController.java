package com.chensoul.diner.controller;

import com.chensoul.commons.model.domain.R;
import com.chensoul.diner.domain.service.SignService;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 签到控制层
 */
@RestController
@RequestMapping("sign")
@AllArgsConstructor
public class SignController {

	private SignService signService;

	/**
	 * 获取用户签到情况 默认当月
	 *
	 * @param date
	 * @return
	 */
	@GetMapping
	public R getSignInfo(final LocalDateTime date) {
		final Map<String, Boolean> map = signService.getSignInfo(date);
		return R.ok(map);
	}

	/**
	 * 获取签到次数 默认当月
	 *
	 * @param date
	 * @return
	 */
	@GetMapping("count")
	public R getSignCount(final LocalDateTime date) {
		final Long count = signService.getSignCount(date);
		return R.ok(count);
	}

	/**
	 * 签到，可以补签
	 *
	 * @param date
	 * @return
	 */
	@PostMapping
	public R sign(@RequestParam(required = false) final LocalDateTime date) {
		final int count = signService.doSign(date);
		return R.ok(count);
	}

}
