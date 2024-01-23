package com.imooc.diners.controller;

import com.imooc.commons.model.domain.ResultInfo;
import com.imooc.commons.model.dto.DinersDTO;
import com.imooc.commons.model.vo.ShortDinerInfo;
import com.imooc.commons.utils.ResultInfoUtil;
import com.imooc.diners.service.DinersService;
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
public class DinersController {

	@Resource
	private DinersService dinersService;

	/**
	 * 根据 ids 查询食客信息
	 *
	 * @param ids
	 * @return
	 */
	@GetMapping("findByIds")
	public ResultInfo<List<ShortDinerInfo>> findByIds(final String ids) {
		final List<ShortDinerInfo> dinerInfos = this.dinersService.findByIds(ids);
		return ResultInfoUtil.buildSuccess(dinerInfos);
	}

	/**
	 * 注册
	 *
	 * @param dinersDTO
	 * @return
	 */
	@PostMapping("register")
	public ResultInfo register(@RequestBody final DinersDTO dinersDTO) {
		return this.dinersService.register(dinersDTO);
	}

	/**
	 * 校验手机号是否已注册
	 *
	 * @param phone
	 * @return
	 */
	@GetMapping("checkPhone")
	public ResultInfo checkPhone(final String phone) {
		this.dinersService.checkPhoneIsRegistered(phone);
		return ResultInfoUtil.buildSuccess();
	}

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
