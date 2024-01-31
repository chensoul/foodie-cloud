package com.chensoul.point.client;

import com.chensoul.commons.model.domain.R;
import com.chensoul.point.domain.entity.Point;
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
