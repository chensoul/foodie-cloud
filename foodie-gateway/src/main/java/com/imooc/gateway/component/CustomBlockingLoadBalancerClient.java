package com.imooc.gateway.component;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import reactor.core.publisher.Mono;

/**
 * https://blog.csdn.net/wufangliang/article/details/123282593
 */
@Slf4j
public class CustomBlockingLoadBalancerClient extends BlockingLoadBalancerClient {
	private final ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory;

	public CustomBlockingLoadBalancerClient(final LoadBalancerClientFactory loadBalancerClientFactory, final LoadBalancerProperties properties) {
		super(loadBalancerClientFactory, properties);
		this.loadBalancerClientFactory = loadBalancerClientFactory;
	}

	public CustomBlockingLoadBalancerClient(final ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory) {
		super(loadBalancerClientFactory);
		this.loadBalancerClientFactory = loadBalancerClientFactory;
	}

	@Override
	public <T> ServiceInstance choose(final String serviceId, final Request<T> request) {
		final ReactiveLoadBalancer<ServiceInstance> loadBalancer = this.loadBalancerClientFactory.getInstance(serviceId);
		if (loadBalancer == null) {
			return null;
		}
		final CompletableFuture<Response<ServiceInstance>> f = CompletableFuture.supplyAsync(() -> {
			final Response<ServiceInstance> loadBalancerResponse = Mono.from(loadBalancer.choose(request)).block();
			return loadBalancerResponse;
		});
		Response<ServiceInstance> loadBalancerResponse = null;
		try {
			loadBalancerResponse = f.get();
		} catch (final Exception e) {
			return null;
		}
		return loadBalancerResponse.getServer();
	}
}
