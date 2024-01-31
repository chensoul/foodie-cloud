package com.chensoul.diner.controller;

import com.chensoul.auth.client.UserClient;
import com.chensoul.auth.entity.User;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HelloController {
	private UserClient userClient;

	@GetMapping("hello")
	public User hello(final Principal principal) {
		return userClient.getCurrentUser().getData();
	}

}
