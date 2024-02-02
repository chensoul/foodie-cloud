package com.chensoul.infrastructure.security;

import com.chensoul.infrastructure.security.oauth2.support.CustomWebResponseExceptionTranslator;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Security 配置类
 */
@AllArgsConstructor
@Configuration
@EnableWebSecurity
@Order(10)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
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

	@Bean
	public WebResponseExceptionTranslator webResponseExceptionTranslator() {
		return new CustomWebResponseExceptionTranslator();
	}
}
