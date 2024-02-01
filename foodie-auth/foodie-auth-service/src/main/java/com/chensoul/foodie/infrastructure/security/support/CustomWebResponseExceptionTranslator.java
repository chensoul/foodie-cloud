package com.chensoul.foodie.infrastructure.security.support;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
public class CustomWebResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {
	@Override
	public ResponseEntity<OAuth2Exception> translate(final Exception e) throws Exception {
		final ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
		final OAuth2Exception originEx = responseEntity.getBody();
		final HttpHeaders headers = responseEntity.getHeaders();

		final CustomOAuth2Exception customOAuth2Exception = CustomOAuth2Exception.from(originEx);
		return new ResponseEntity<>(customOAuth2Exception, headers, responseEntity.getStatusCode());
	}
}
