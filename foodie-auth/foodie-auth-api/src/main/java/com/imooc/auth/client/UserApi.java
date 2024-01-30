package com.imooc.auth.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.auth.entity.User;
import com.imooc.auth.model.dto.UserAddRequest;
import com.imooc.commons.model.domain.R;
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
	@PostMapping
	R<Void> register(@RequestBody final UserAddRequest userAddRequest);

	@GetMapping("/phone")
	R<Void> checkPhone(final String phone);

	@GetMapping("/page")
	R<Page<User>> page(final Page<User> page);

	@GetMapping("/findByIds")
	R<List<User>> findByIds(@RequestParam("ids") final Set<Long> userIds);

	@GetMapping("/info")
	R<User> getCurrentUser();

	@GetMapping("/logout")
	R<Void> logout(String token);
}
