package com.imooc.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.dto.UserAddRequest;
import com.imooc.commons.model.entity.User;
import com.imooc.commons.model.vo.ShortUserInfo;
import com.imooc.user.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 食客服务控制层
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
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

	@GetMapping("page")
	public R<Page<User>> page(final Page<User> page) {
		return R.ok(this.userService.page(page));
	}

	@GetMapping("findByIds")
	public R<List<ShortUserInfo>> findByIds(final String ids) {
		return R.ok(this.userService.findByIds(ids));
	}

}
