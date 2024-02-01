package com.chensoul.auth.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 客户端配置类
 */
@ConfigurationProperties(prefix = "client.oauth2")
@Data
public class ClientOAuth2Properties {

	private String clientId;

	private String secret;

	private String[] grantTypes;

	private int tokenValidityTime;

	private int refreshTokenValidityTime;

	private String[] scopes;

}