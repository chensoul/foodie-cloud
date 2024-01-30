package com.imooc.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.auth.client.UserApi;
import com.imooc.auth.entity.User;
import com.imooc.auth.model.LoggedUser;
import com.imooc.auth.model.dto.UserAddRequest;
import com.imooc.auth.service.UserService;
import com.imooc.commons.model.domain.R;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户中心
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController implements UserApi {
	private UserService userService;
	private RedisTokenStore redisTokenStore;

	/**
	 * 注册
	 *
	 * @param userAddRequest
	 * @return
	 */
	@Override
	public R<Void> register(final UserAddRequest userAddRequest) {
		this.userService.register(userAddRequest);
		return R.ok();
	}

	/**
	 * 校验手机号是否已注册
	 *
	 * @param phone
	 * @return
	 */
	@Override
	public R<Void> checkPhone(final String phone) {
		this.userService.checkPhoneIsRegistered(phone);
		return R.ok();
	}

	@Override
	public R<Page<User>> page(final Page<User> page) {
		return R.ok(this.userService.page(page));
	}

	@Override
	public R<List<User>> findByIds(final Set<Long> userIds) {
		return R.ok(this.userService.findByIds(userIds));
	}

	@Override
	public R<User> getCurrentUser() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Assert.notNull(authentication.getPrincipal(), "获取当前用户信息失败");

		final LoggedUser loggedUser = (LoggedUser) authentication.getPrincipal();
		return R.ok(loggedUser);
	}

	/**
	 * 安全退出
	 *
	 * @return
	 */
	@Override
	public R<Void> logout(String token) {
		if (StringUtils.isBlank(token)) {
			return R.ok();
		}
		if (token.toLowerCase().contains("bearer ".toLowerCase())) {
			token = token.toLowerCase().replace("bearer ", "");
		}
		final OAuth2AccessToken oAuth2AccessToken = this.redisTokenStore.readAccessToken(token);
		if (oAuth2AccessToken != null) {
			this.redisTokenStore.removeAccessToken(oAuth2AccessToken);
			final OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
			this.redisTokenStore.removeRefreshToken(refreshToken);
		}
		return R.ok();
	}

}
