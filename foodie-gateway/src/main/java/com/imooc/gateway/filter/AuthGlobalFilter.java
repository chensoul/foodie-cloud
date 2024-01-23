package com.imooc.gateway.filter;

import com.imooc.gateway.component.HandleException;
import com.imooc.gateway.config.IgnoreUrlsConfig;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局过滤器
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

	@Resource
	private IgnoreUrlsConfig ignoreUrlsConfig;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private HandleException handleException;

	/**
	 * 身份校验处理
	 *
	 * @param exchange
	 * @param chain
	 * @return
	 */
	@Override
	public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
		// 判断当前的请求是否在白名单中
		final AntPathMatcher pathMatcher = new AntPathMatcher();
		boolean flag = false;
		final String path = exchange.getRequest().getURI().getPath();
		for (final String url : this.ignoreUrlsConfig.getUrls())
			if (pathMatcher.match(url, path)) {
				flag = true;
				break;
			}
		// 白名单放行
		if (flag) {
			return chain.filter(exchange);
		}
		final String access_token = exchange.getRequest().getQueryParams().getFirst("access_token");
		if (StringUtils.isBlank(access_token)) {
			return this.handleException.writeError(exchange, "请登录");
		}
		final String checkTokenUrl = "http://foodie-oauth2-server/oauth/check_token?token=".concat(access_token);
		try {
			final ResponseEntity<String> entity = this.restTemplate.postForEntity(checkTokenUrl, null, String.class);
			if (entity.getStatusCode() != HttpStatus.OK) {
				return this.handleException.writeError(exchange,
					"Token was not recognised, token: ".concat(access_token));
			}
			if (StringUtils.isBlank(entity.getBody())) {
				return this.handleException.writeError(exchange,
					"This token is invalid: ".concat(access_token));
			}
		} catch (final Exception e) {
			log.error("Token was not recognised, token: {}", access_token, e);
			return this.handleException.writeError(exchange,
				"Token was not recognised, token: ".concat(access_token));
		}
		// 放行
		return chain.filter(exchange);
	}

	/**
	 * 网关过滤器的排序，数字越小优先级越高
	 *
	 * @return
	 */
	@Override
	public int getOrder() {
		return 0;
	}

}
