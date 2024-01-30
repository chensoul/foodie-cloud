package com.imooc.auth.controller;

import com.imooc.auth.service.VerifyCodeService;
import com.imooc.commons.model.domain.R;
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
