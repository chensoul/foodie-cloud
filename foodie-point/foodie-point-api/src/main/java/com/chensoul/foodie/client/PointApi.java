package com.chensoul.foodie.client;

import com.chensoul.core.model.R;
import com.chensoul.foodie.domain.point.entity.Point;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
public interface PointApi {
	@PostMapping("/point")
	R<Point> addPoint(@RequestBody Point point);
}
