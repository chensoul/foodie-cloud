package com.chensoul;

import com.chensoul.client.UserClient;
import com.chensoul.domain.user.entity.User;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

	@Test
	void getCurrentUser() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			User user = userClient.getCurrentUser().getData();
			log.warn("user={}", user);

			status();
			Thread.sleep(500);
		}
	}

	private void status() {
		CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("auth-service");
		CircuitBreaker.Metrics metrics = breaker.getMetrics();
		log.info("state={},metrics[failureRate={},bufferedCalls={},failedCalls={},successCalls={},maxBufferCalls={},notPermittedCalls={}]"
			, breaker.getState(), metrics.getFailureRate(), metrics.getNumberOfBufferedCalls(), metrics.getNumberOfFailedCalls()
			, metrics.getNumberOfSuccessfulCalls(), metrics.getNumberOfBufferedCalls(), metrics.getNumberOfNotPermittedCalls());
	}

}
