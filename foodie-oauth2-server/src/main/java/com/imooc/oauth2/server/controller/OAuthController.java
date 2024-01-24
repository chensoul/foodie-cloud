package com.imooc.oauth2.server.controller;

import com.imooc.commons.model.domain.R;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Oauth2 控制器
 */
@RestController
@RequestMapping("oauth")
public class OAuthController {

	@Resource
	private TokenEndpoint tokenEndpoint;

	@PostMapping("token")
	public R postAccessToken(final Principal principal, @RequestParam final Map<String, String> parameters)
		throws HttpRequestMethodNotSupportedException {
		return OAuthController.custom(this.tokenEndpoint.postAccessToken(principal, parameters).getBody());
	}

	/**
	 * 自定义 Token 返回对象
	 *
	 * @param accessToken
	 * @return
	 */
	private static R custom(final OAuth2AccessToken accessToken) {
		final DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
		final Map<String, Object> data = new LinkedHashMap(token.getAdditionalInformation());
		data.put("accessToken", token.getValue());
		data.put("expireIn", token.getExpiresIn());
		data.put("scopes", token.getScope());
		
		if (token.getRefreshToken() != null) {
			data.put("refreshToken", token.getRefreshToken().getValue());
		}
		return R.ok(data);
	}

}
