package com.imooc.oauth2.server.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Security 配置类
 */
@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private RedisConnectionFactory redisConnectionFactory;

	@Bean
	public RedisTokenStore redisTokenStore() {
		final RedisTokenStore redisTokenStore = new RedisTokenStore(this.redisConnectionFactory);
		redisTokenStore.setPrefix("TOKEN:"); // 设置key的层级前缀，方便查询
		return redisTokenStore;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.csrf().disable()
			.authorizeRequests()
			.antMatchers("/oauth/**", "/actuator/**").permitAll()
			.and()
			.authorizeRequests().anyRequest().authenticated();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/**
	 * OAuth2 AccessDeniedHandler
	 */
	@Bean
	public AccessDeniedHandler accessDeniedHandler(final WebResponseExceptionTranslator exceptionTranslator) {
		final OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
		accessDeniedHandler.setExceptionTranslator(exceptionTranslator);
		return accessDeniedHandler;
	}

	/**
	 * OAuth2 AuthenticationEntryPoint
	 */
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint(final WebResponseExceptionTranslator exceptionTranslator) {
		final OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		authenticationEntryPoint.setExceptionTranslator(exceptionTranslator);
		return authenticationEntryPoint;
	}

}
