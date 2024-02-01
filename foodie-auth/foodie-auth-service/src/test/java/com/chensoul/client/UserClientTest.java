package com.chensoul.client;

import com.chensoul.core.model.R;
import com.chensoul.foodie.client.UserClient;
import com.chensoul.foodie.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
@SpringBootTest
@EnableFeignClients
class UserClientTest {
	@Autowired
	private UserClient userClient;

	@Test
	public void test() {
		final R<User> currentUser = userClient.getCurrentUser();

	}

}
