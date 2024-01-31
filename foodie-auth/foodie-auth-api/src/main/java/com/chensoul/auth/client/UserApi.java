package com.chensoul.auth.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chensoul.auth.entity.User;
import com.chensoul.auth.model.dto.UserAddRequest;
import com.chensoul.commons.model.domain.R;
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

	@GetMapping("/user/findByIds")
	R<List<User>> findByIds(@RequestParam("ids") final Set<Long> userIds);

	@GetMapping("/user/info")
	R<User> getCurrentUser();

	@GetMapping("/user/logout")
	R<Void> logout(String token);
}
