package com.imooc.diners.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.diners.service.SendVerifyCodeService;
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
	public ResultInfo send(final String phone) {
		this.sendVerifyCodeService.send(phone);
		return ResultInfoUtil.buildSuccess("发送成功");
	}

}
