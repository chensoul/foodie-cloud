package com.imooc.oauth2.server.controller;

import com.imooc.commons.model.domain.R;
import com.imooc.commons.model.entity.Diner;
import com.imooc.oauth2.server.model.DinerUserDetails;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户中心
 */
@RestController
@AllArgsConstructor
public class UserController {
	private RedisTokenStore redisTokenStore;

	@GetMapping("user/me")
	public static R<Diner> getCurrentUser(final Authentication authentication) {
		final DinerUserDetails dinerUserDetails = (DinerUserDetails) authentication.getPrincipal();
		final Diner diner = new Diner();
		BeanUtils.copyProperties(dinerUserDetails, diner);
		return R.ok(diner);
	}

	/**
	 * 安全退出
	 *
	 * @param access_token
	 * @param authorization
	 * @return
	 */
	@GetMapping("user/logout")
	public R logout(String access_token, final String authorization) {
		if (StringUtils.isBlank(access_token)) {
			access_token = authorization;
		}
		if (StringUtils.isBlank(access_token)) {
			return R.ok();
		}
		if (access_token.toLowerCase().contains("bearer ".toLowerCase())) {
			access_token = access_token.toLowerCase().replace("bearer ", "");
		}
		final OAuth2AccessToken oAuth2AccessToken = this.redisTokenStore.readAccessToken(access_token);
		if (oAuth2AccessToken != null) {
			this.redisTokenStore.removeAccessToken(oAuth2AccessToken);
			final OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
			this.redisTokenStore.removeRefreshToken(refreshToken);
		}
		return R.ok();
	}

}
