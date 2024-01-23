package com.imooc.oauth2.server.config;

import com.imooc.commons.model.domain.SignInIdentity;
import com.imooc.oauth2.server.service.UserService;
import java.util.LinkedHashMap;
import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 授权服务
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	@Resource
	private RedisTokenStore redisTokenStore;
	@Resource
	private AuthenticationManager authenticationManager;
	@Resource
	private PasswordEncoder passwordEncoder;
	@Resource
	private ClientOAuth2DataConfiguration clientOAuth2DataConfiguration;
	@Resource
	private UserService userService;

	/**
	 * 配置令牌端点安全约束
	 *
	 * @param security
	 * @throws Exception
	 */
	@Override
	public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
		// 允许访问 token 的公钥，默认 /oauth/token_key 是受保护的
		security.tokenKeyAccess("permitAll()")
			// 允许检查 token 的状态，默认 /oauth/check_token 是受保护的
			.checkTokenAccess("permitAll()");
	}

	/**
	 * 客户端配置 - 授权模型
	 *
	 * @param clients
	 * @throws Exception
	 */
	@Override
	public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient(this.clientOAuth2DataConfiguration.getClientId()) // 客户端标识 ID
			.secret(this.passwordEncoder.encode(this.clientOAuth2DataConfiguration.getSecret())) // 客户端安全码
			.authorizedGrantTypes(this.clientOAuth2DataConfiguration.getGrantTypes()) // 授权类型
			.accessTokenValiditySeconds(this.clientOAuth2DataConfiguration.getTokenValidityTime()) // token 有效期
			.refreshTokenValiditySeconds(this.clientOAuth2DataConfiguration.getRefreshTokenValidityTime()) // 刷新 token 的有效期
			.scopes(this.clientOAuth2DataConfiguration.getScopes()); // 客户端访问范围
	}

	/**
	 * 配置授权以及令牌的访问端点和令牌服务
	 *
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(this.authenticationManager)
			// 具体登录的方法
			.userDetailsService(this.userService)
			// token 存储的方式：Redis
			.tokenStore(this.redisTokenStore)
			// 令牌增强对象，增强返回的结果
			.tokenEnhancer((accessToken, authentication) -> {
				// 获取登录用户的信息，然后设置
				final SignInIdentity signInIdentity = (SignInIdentity) authentication.getPrincipal();
				final LinkedHashMap<String, Object> map = new LinkedHashMap<>();
				map.put("nickname", signInIdentity.getNickname());
				map.put("avatarUrl", signInIdentity.getAvatarUrl());
				final DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
				token.setAdditionalInformation(map);
				return token;
			});
	}

}
