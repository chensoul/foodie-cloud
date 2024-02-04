package com.chensoul.infrastructure.security.oauth2.authorizationServer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 客户端配置类
 */
@ConfigurationProperties(prefix = "client.oauth2")
@Data
@RefreshScope
public class ClientOAuth2Properties {

	private String clientId;

	private String secret;

	private String[] grantTypes;

	private int tokenValidityTime;

	private int refreshTokenValidityTime;

	private String[] scopes;

}
