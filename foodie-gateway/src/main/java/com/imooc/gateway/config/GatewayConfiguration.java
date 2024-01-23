package com.imooc.gateway.config;

import com.imooc.gateway.component.CustomBlockingLoadBalancerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
@Configuration
public class GatewayConfiguration {
	@Autowired
	private LoadBalancerClientFactory loadBalancerClientFactory;

	@Bean
	public LoadBalancerClient blockingLoadBalancerClient() {
		return new CustomBlockingLoadBalancerClient(this.loadBalancerClientFactory);
	}
}
