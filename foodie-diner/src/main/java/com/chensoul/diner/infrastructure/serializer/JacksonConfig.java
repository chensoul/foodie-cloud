package com.chensoul.diner.infrastructure.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
@Configuration
public class JacksonConfig {
	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return new JacksonObjectMapper();
	}
}
