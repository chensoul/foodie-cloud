package com.chensoul;

import com.chensoul.client.UserClient;
import com.chensoul.domain.user.entity.User;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
@Slf4j
@SpringBootTest(classes = AuthApplication.class)
class UserApiTest {
	@Autowired
	private UserClient userClient;
	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	void getCurrentUser() throws InterruptedException {
		for (int i = 0; i < 20; i++) {
			User user = userClient.getCurrentUser().getData();
			log.warn("user={}", user);

			status();
		}
	}

	private void status() {
		CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("authservice");
		CircuitBreaker.Metrics metrics = breaker.getMetrics();
		log.info("state={},metrics[failureRate={},bufferedCalls={},failedCalls={},successCalls={},maxBufferCalls={},notPermittedCalls={}]"
			, breaker.getState(), metrics.getFailureRate(), metrics.getNumberOfBufferedCalls(), metrics.getNumberOfFailedCalls()
			, metrics.getNumberOfSuccessfulCalls(), metrics.getNumberOfBufferedCalls(), metrics.getNumberOfNotPermittedCalls());
	}

	@Test
	void limit() throws Exception {
		for (int i = 1; i <= 99; i++) {
			try {
				ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:6666/user/info", String.class);
				log.info("status code {} {}", String.format("%02d", i), response.getStatusCode());
			} catch (HttpClientErrorException e) {
				log.error("status code {}", e.getStatusCode());
			}
			Thread.sleep(400);
		}
	}

}
