package com.imooc.diner.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.diner.service.SignService;
import java.time.LocalDateTime;
import java.util.Map;
import javax.annotation.Resource;
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
public class SignController {

	@Resource
	private SignService signService;

	/**
	 * 获取用户签到情况 默认当月
	 *
	 * @param access_token
	 * @param date
	 * @return
	 */
	@GetMapping
	public R getSignInfo(final String access_token, final LocalDateTime date) {
		final Map<String, Boolean> map = this.signService.getSignInfo(access_token, date);
		return R.ok(map);
	}

	/**
	 * 获取签到次数 默认当月
	 *
	 * @param access_token
	 * @param date
	 * @return
	 */
	@GetMapping("count")
	public R getSignCount(final String access_token, final LocalDateTime date) {
		final Long count = this.signService.getSignCount(access_token, date);
		return R.ok(count);
	}

	/**
	 * 签到，可以补签
	 *
	 * @param access_token
	 * @param date
	 * @return
	 */
	@PostMapping
	public R sign(final String access_token,
				  @RequestParam(required = false) final LocalDateTime date) {
		final int count = this.signService.doSign(access_token, date);
		return R.ok(count);
	}

}
