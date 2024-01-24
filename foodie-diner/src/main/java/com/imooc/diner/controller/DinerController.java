package com.imooc.diner.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.dto.DinerRequest;
import com.imooc.commons.model.vo.ShortDinerInfo;
import com.imooc.diner.service.DinerService;
import com.imooc.diner.vo.DinerLoginVO;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 食客服务控制层
 */
@RestController
public class DinerController {

	@Resource
	private DinerService dinerService;

	/**
	 * 登录
	 *
	 * @param account
	 * @param password
	 * @return
	 */
	@GetMapping("signin")
	public R<DinerLoginVO> signIn(@NotBlank(message = "请输入登录帐号") final String account,
								  @NotBlank(message = "请输入登录密码") final String password) {
		return this.dinerService.signIn(account, password);
	}

	/**
	 * 注册
	 *
	 * @param dinerRequest
	 * @return
	 */
	@PostMapping("register")
	public R register(@RequestBody final DinerRequest dinerRequest) {
		return this.dinerService.register(dinerRequest);
	}

	/**
	 * 校验手机号是否已注册
	 *
	 * @param phone
	 * @return
	 */
	@GetMapping("checkPhone")
	public R checkPhone(final String phone) {
		this.dinerService.checkPhoneIsRegistered(phone);
		return R.ok();
	}

	@GetMapping("findByIds")
	public R<List<ShortDinerInfo>> findByIds(final String ids) {
		return R.ok(this.dinerService.findByIds(ids));
	}

}
