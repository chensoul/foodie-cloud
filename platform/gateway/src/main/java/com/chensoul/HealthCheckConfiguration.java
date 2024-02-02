//package com.chensoul;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//import static java.util.logging.Level.FINE;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.ReactiveHealthContributor;
//import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//public class HealthCheckConfiguration {
//
//	private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);
//
//	private final WebClient webClient;
//
//	@Autowired
//	public HealthCheckConfiguration(final WebClient.Builder webClientBuilder) {
//		this.webClient = webClientBuilder.build();
//	}
//
//	@Bean
//	ReactiveHealthContributor healthcheckMicroservices() {
//		final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();
//
//		registry.put("auth", () -> this.getHealth("http://auth"));
//		registry.put("diner", () -> this.getHealth("http://foodie-diner"));
//		registry.put("point", () -> this.getHealth("http://foodie-point"));
//		registry.put("feed", () -> this.getHealth("http://foodie-feed"));
//		registry.put("follow", () -> this.getHealth("http://foodie-follow"));
//		registry.put("order", () -> this.getHealth("http://foodie-order"));
//		registry.put("restaurant", () -> this.getHealth("http://foodie-restaurant"));
//
//		return CompositeReactiveHealthContributor.fromMap(registry);
//	}
//
//	private Mono<Health> getHealth(final String baseUrl) {
//		final String url = baseUrl + "/actuator/health";
//		LOG.debug("Setting up a call to the Health API on URL: {}", url);
//		return this.webClient.get().uri(url).retrieve().bodyToMono(String.class)
//			.map(s -> new Health.Builder().up().build())
//			.onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
//			.log(LOG.getName(), FINE);
//	}
//
//}
