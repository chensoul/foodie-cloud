package com.chensoul.client;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "foodie-foodie-service")
public interface PointClient extends PointApi {
}
