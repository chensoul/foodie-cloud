package com.chensoul.controller;

import com.chensoul.core.model.R;
import com.chensoul.domain.model.NearMeUserVO;
import com.chensoul.domain.service.NearMeService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("nearme")
@AllArgsConstructor
public class NearMeController {
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
