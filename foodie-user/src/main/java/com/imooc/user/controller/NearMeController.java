package com.imooc.user.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.vo.NearMeUserVO;
import com.imooc.user.service.NearMeService;
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
	 * @param lon
	 * @param lat
	 * @return
	 */
	@PostMapping
	public R updateUserLocation(@RequestParam final Float lon,
								@RequestParam final Float lat) {
		this.nearMeService.updateUserLocation(lon, lat);
		return R.ok();
	}

	/**
	 * 获取附近的人
	 *
	 * @param radius
	 * @param lon
	 * @param lat
	 * @return
	 */
	@GetMapping
	public R nearMe(final Integer radius,
					final Float lon, final Float lat) {
		final List<NearMeUserVO> nearMe = this.nearMeService.findNearMe(radius, lon, lat);
		return R.ok(nearMe);
	}

}
