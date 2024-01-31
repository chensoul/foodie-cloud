package com.chensoul.auth.controller;

import com.chensoul.auth.service.VerifyCodeService;
import com.chensoul.commons.model.domain.R;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发送验证码控制层
 */
@RestController
public class VerifyCodeController {
	@Resource
	private VerifyCodeService verifyCodeService;

	/**
	 * 发送验证码
	 *
	 * @param phone
	 * @return
	 */
	@GetMapping("send")
	public R send(final String phone) {
		this.verifyCodeService.send(phone);
		return R.ok();
	}
}
