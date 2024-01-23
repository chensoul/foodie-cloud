package com.imooc.diners.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.diners.service.DinersService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 食客服务控制层
 */
@RestController
public class DinersController {

	@Resource
	private DinersService dinersService;

	/**
	 * 登录
	 *
	 * @param account
	 * @param password
	 * @return
	 */
	@GetMapping("signin")
	public ResultInfo signIn(final String account, final String password) {
		return this.dinersService.signIn(account, password);
	}

}
