package com.chensoul.client;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "auth-center-service")
public interface UserClient extends UserApi {
}
