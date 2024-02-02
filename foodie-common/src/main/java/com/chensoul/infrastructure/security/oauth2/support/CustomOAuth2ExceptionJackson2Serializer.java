package com.chensoul.infrastructure.security.oauth2.support;

import com.chensoul.core.model.R;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomOAuth2ExceptionJackson2Serializer extends StdSerializer<CustomOAuth2Exception> {

	private static final long serialVersionUID = -6316948473594565323L;

	protected CustomOAuth2ExceptionJackson2Serializer() {
		super(CustomOAuth2Exception.class);
	}

	@Override
	public void serialize(final CustomOAuth2Exception e, final JsonGenerator jgen, final SerializerProvider serializerProvider) throws IOException {
		final R<String> errorResult = R.error(e.getMessage());
		log.error("{}", errorResult, e);

		jgen.writeObject(errorResult);
	}
}
