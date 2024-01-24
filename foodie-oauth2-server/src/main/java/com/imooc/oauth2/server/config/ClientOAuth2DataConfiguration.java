package com.imooc.oauth2.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 客户端配置类
 */
@Component
@ConfigurationProperties(prefix = "client.oauth2")
@Data
public class ClientOAuth2DataConfiguration {

	private String clientId;

	private String secret;

	private String[] grantTypes;

	private int tokenValidityTime;

	private int refreshTokenValidityTime;

	private String[] scopes;

}
