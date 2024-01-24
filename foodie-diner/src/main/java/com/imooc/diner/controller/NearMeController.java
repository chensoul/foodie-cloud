package com.imooc.diner.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.vo.NearMeDinerVO;
import com.imooc.diner.service.NearMeService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("nearme")
public class NearMeController {

	@Resource
	private NearMeService nearMeService;

	/**
	 * 更新食客坐标
	 *
	 * @param access_token
	 * @param lon
	 * @param lat
	 * @return
	 */
	@PostMapping
	public R updateDinerLocation(final String access_token,
								 @RequestParam final Float lon,
								 @RequestParam final Float lat) {
		this.nearMeService.updateDinerLocation(access_token, lon, lat);
		return R.ok();
	}

	/**
	 * 获取附近的人
	 *
	 * @param access_token
	 * @param radius
	 * @param lon
	 * @param lat
	 * @return
	 */
	@GetMapping
	public R nearMe(final String access_token,
					final Integer radius,
					final Float lon, final Float lat) {
		final List<NearMeDinerVO> nearMe = this.nearMeService.findNearMe(access_token, radius, lon, lat);
		return R.ok(nearMe);
	}

}
