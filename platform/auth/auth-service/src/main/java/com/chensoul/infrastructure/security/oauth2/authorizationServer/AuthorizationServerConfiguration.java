package com.chensoul.infrastructure.security.oauth2.authorizationServer;

import com.chensoul.domain.user.model.LoggedUser;
import com.chensoul.domain.user.service.CustomUserDetailsService;
import java.util.LinkedHashMap;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 授权服务
 */
@AllArgsConstructor
@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties(ClientOAuth2Properties.class)
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
	private AuthenticationManager authenticationManager;
	private PasswordEncoder passwordEncoder;
	private ClientOAuth2Properties clientOAuth2Properties;
	private CustomUserDetailsService customUserDetailsService;
	private WebResponseExceptionTranslator webResponseExceptionTranslator;
	private RedisConnectionFactory redisConnectionFactory;

	@Bean
	public RedisTokenStore redisTokenStore() {
		final RedisTokenStore redisTokenStore = new RedisTokenStore(this.redisConnectionFactory);
		redisTokenStore.setPrefix("foodie:oauth:"); // 设置key的层级前缀，方便查询
		return redisTokenStore;
	}

	/**
	 * 配置令牌端点安全约束
	 *
	 * @param security
	 * @throws Exception
	 */
	@Override
	public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()")
			.checkTokenAccess("permitAll()")
			.allowFormAuthenticationForClients();
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
			.userDetailsService(this.customUserDetailsService)
			.tokenStore(this.redisTokenStore())
			.tokenEnhancer((accessToken, authentication) -> {
				final DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;

				//兼容客户端模式
				if(authentication.getPrincipal() instanceof LoggedUser){
					final LoggedUser loggedUser = (LoggedUser) authentication.getPrincipal();
					final LinkedHashMap<String, Object> map = new LinkedHashMap<>();
					map.put("nickname", loggedUser.getNickname());
					map.put("avatar", loggedUser.getAvatar());
					token.setAdditionalInformation(map);
				}

				return token;
			}).exceptionTranslator(this.webResponseExceptionTranslator)
			.allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET);
	}

}
