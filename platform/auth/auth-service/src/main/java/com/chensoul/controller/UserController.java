package com.chensoul.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chensoul.client.UserApi;
import com.chensoul.core.model.R;
import com.chensoul.domain.user.entity.User;
import com.chensoul.domain.user.model.LoggedUser;
import com.chensoul.domain.user.model.UserAddRequest;
import com.chensoul.domain.user.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户中心
 */
@RestController
@AllArgsConstructor
public class UserController implements UserApi {
	private UserService userService;
	private UserDetailsService userDetailsService;
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
	public R<List<User>> list(final Set<Long> userIds) {
		return R.ok(this.userService.list(Wrappers.<User>lambdaQuery()
			.in(CollectionUtils.isNotEmpty(userIds), User::getId, userIds)));
	}

	@Override
	@Retry(name = "authservice")
	@RateLimiter(name = "authservice")
	public R<User> getCurrentUser() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Assert.notNull(authentication.getPrincipal(), "获取当前用户信息失败");

		if (authentication.getPrincipal() instanceof LoggedUser) {
			final LoggedUser loggedUser = (LoggedUser) authentication.getPrincipal();
			return R.ok(loggedUser);
		} else {
			return R.ok((LoggedUser) this.userDetailsService.loadUserByUsername(authentication.getPrincipal().toString()));
		}
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
