package com.imooc.diners.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.diners.service.SignService;
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
	 * @param dateStr
	 * @return
	 */
	@GetMapping
	public ResultInfo getSignInfo(final String access_token, final String dateStr) {
		final Map<String, Boolean> map = this.signService.getSignInfo(access_token, dateStr);
		return ResultInfoUtil.buildSuccess(map);
	}

	/**
	 * 获取签到次数 默认当月
	 *
	 * @param access_token
	 * @param date
	 * @return
	 */
	@GetMapping("count")
	public ResultInfo getSignCount(final String access_token, final String date) {
		final Long count = this.signService.getSignCount(access_token, date);
		return ResultInfoUtil.buildSuccess(count);
	}

	/**
	 * 签到，可以补签
	 *
	 * @param access_token
	 * @param date
	 * @return
	 */
	@PostMapping
	public ResultInfo sign(final String access_token,
						   @RequestParam(required = false) final String date) {
		final int count = this.signService.doSign(access_token, date);
		return ResultInfoUtil.buildSuccess(count);
	}

}
