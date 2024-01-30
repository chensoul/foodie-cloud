package com.imooc.auth.client;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "foodie-auth-service", path = "/user")
public interface UserClient extends UserApi {
}
