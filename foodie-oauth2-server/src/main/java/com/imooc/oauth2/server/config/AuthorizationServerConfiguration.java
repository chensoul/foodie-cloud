package com.imooc.oauth2.server.config;

import com.imooc.oauth2.server.component.CustomWebResponseExceptionTranslator;
import com.imooc.oauth2.server.model.DinerUserDetails;
import com.imooc.oauth2.server.service.UserService;
import java.util.LinkedHashMap;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@AllArgsConstructor
@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties(ClientOAuth2Properties.class)
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
	private RedisTokenStore redisTokenStore;
	private AuthenticationManager authenticationManager;
	private PasswordEncoder passwordEncoder;
	private ClientOAuth2Properties clientOAuth2Properties;
	private UserService userService;

	/**
	 * 配置令牌端点安全约束
	 *
	 * @param security
	 * @throws Exception
	 */
	@Override
	public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()")
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
		clients.inMemory().withClient(this.clientOAuth2Properties.getClientId()) // 客户端标识 ID
			.secret(this.passwordEncoder.encode(this.clientOAuth2Properties.getSecret())) // 客户端安全码
			.authorizedGrantTypes(this.clientOAuth2Properties.getGrantTypes()) // 授权类型
			.accessTokenValiditySeconds(this.clientOAuth2Properties.getTokenValidityTime()) // token 有效期
			.refreshTokenValiditySeconds(this.clientOAuth2Properties.getRefreshTokenValidityTime()) // 刷新 token 的有效期
			.scopes(this.clientOAuth2Properties.getScopes()); // 客户端访问范围
	}

	/**
	 * 配置授权以及令牌的访问端点和令牌服务
	 *
	 * @param endpoint
	 * @throws Exception
	 */
	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoint) throws Exception {
		endpoint.authenticationManager(this.authenticationManager)
			.userDetailsService(this.userService)
			.tokenStore(this.redisTokenStore)
			.tokenEnhancer((accessToken, authentication) -> {
				final DinerUserDetails dinerUserDetails = (DinerUserDetails) authentication.getPrincipal();
				final LinkedHashMap<String, Object> map = new LinkedHashMap<>();
				map.put("nickname", dinerUserDetails.getNickname());
				map.put("avatarUrl", dinerUserDetails.getAvatarUrl());
				final DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
				token.setAdditionalInformation(map);
				return token;
			}).exceptionTranslator(new CustomWebResponseExceptionTranslator());
	}

}
