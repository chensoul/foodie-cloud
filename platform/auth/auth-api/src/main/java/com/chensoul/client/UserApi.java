package com.chensoul.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chensoul.core.model.R;
import com.chensoul.domain.user.entity.User;
import com.chensoul.domain.user.model.UserAddRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */

public interface UserApi {
	@PostMapping("/user")
	R<Void> register(@RequestBody final UserAddRequest userAddRequest);

	@GetMapping("/user/phone")
	R<Void> checkPhone(final String phone);

	@GetMapping("/user/page")
	R<Page<User>> page(final Page<User> page);

	@GetMapping("/user/list")
	R<List<User>> list(@RequestParam("ids") final Set<Long> userIds);

	@GetMapping("/user/info")
	@CircuitBreaker(name = "auth-service", fallbackMethod = "getUserFallback")
	R<User> getCurrentUser();

	@GetMapping("/user/logout")
	R<Void> logout(String token);

	default R<User> getUserFallback(Exception e) {
		return R.ok();
	}
}
