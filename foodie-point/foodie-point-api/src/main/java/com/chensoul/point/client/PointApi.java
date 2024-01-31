package com.chensoul.point.client;

import com.chensoul.commons.model.domain.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
public interface PointApi {
	@PostMapping("/point")
	R<Void> addPoint(@RequestParam(required = false) final Long userId,
					 @RequestParam(required = false) final Integer point,
					 @RequestParam(required = false) final Integer type);
}
