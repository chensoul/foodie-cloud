package com.imooc.diner.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.dto.UserAddRequest;
import com.imooc.commons.model.vo.ShortDinerInfo;
import com.imooc.diner.service.UserService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 食客服务控制层
 */
@RestController
public class UserController {

	@Resource
	private UserService userService;

	/**
	 * 注册
	 *
	 * @param userAddRequest
	 * @return
	 */
	@PostMapping("register")
	public R register(@RequestBody final UserAddRequest userAddRequest) {
		this.userService.register(userAddRequest);
		return R.ok();
	}

	/**
	 * 校验手机号是否已注册
	 *
	 * @param phone
	 * @return
	 */
	@GetMapping("checkPhone")
	public R checkPhone(final String phone) {
		this.userService.checkPhoneIsRegistered(phone);
		return R.ok();
	}

	@GetMapping("findByIds")
	public R<List<ShortDinerInfo>> findByIds(final String ids) {
		return R.ok(this.userService.findByIds(ids));
	}

}
