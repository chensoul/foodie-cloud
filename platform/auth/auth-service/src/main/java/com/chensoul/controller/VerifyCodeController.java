package com.chensoul.controller;

import com.chensoul.core.model.R;
import com.chensoul.domain.user.service.VerifyCodeService;
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
        this.verifyCodeService.send(phone);
		return R.ok();
	}
}
