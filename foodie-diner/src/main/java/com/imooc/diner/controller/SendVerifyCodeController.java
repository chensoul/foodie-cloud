package com.imooc.diner.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.diner.service.SendVerifyCodeService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发送验证码控制层
 */
@RestController
public class SendVerifyCodeController {
	@Resource
	private SendVerifyCodeService sendVerifyCodeService;

	/**
	 * 发送验证码
	 *
	 * @param phone
	 * @return
	 */
	@GetMapping("send")
	public R send(final String phone) {
		this.sendVerifyCodeService.send(phone);
		return R.ok();
	}
}
