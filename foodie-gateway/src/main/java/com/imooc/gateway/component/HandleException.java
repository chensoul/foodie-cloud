package com.imooc.gateway.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.commons.constant.ApiConstant;
import com.imooc.commons.model.domain.R;
import java.nio.charset.Charset;
import javax.annotation.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class HandleException {

	@Resource
	private ObjectMapper objectMapper;

	public Mono<Void> writeError(final ServerWebExchange exchange, final String error) {
		final ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.OK);
		response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		final R r = R.error(ApiConstant.NO_LOGIN_CODE, ApiConstant.NO_LOGIN_MESSAGE);
		DataBuffer buffer = null;
		try {
			final String resultInfoJson = this.objectMapper.writeValueAsString(r);
			buffer = response.bufferFactory().wrap(resultInfoJson.getBytes(Charset.forName("UTF-8")));
		} catch (final JsonProcessingException ex) {
			ex.printStackTrace();
		}

		return response.writeWith(Mono.just(buffer));
	}

}
