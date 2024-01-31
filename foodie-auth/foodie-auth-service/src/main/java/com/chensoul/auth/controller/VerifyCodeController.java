package com.chensoul.auth.controller;

import com.chensoul.auth.domain.service.VerifyCodeService;
import com.chensoul.commons.model.domain.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发送验证码控制层
 */
@RestController
@AllArgsConstructor
public class VerifyCodeController {
	private VerifyCodeService verifyCodeService;

	/**
	 * 发送验证码
	 *
	 * @param phone
	 * @return
	 */
	@GetMapping("send")
	public R send(final String phone) {
		verifyCodeService.send(phone);
		return R.ok();
	}
}
