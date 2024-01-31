package com.chensoul.auth.service;

import com.chensoul.auth.entity.User;
import com.chensoul.auth.mapper.UserMapper;
import com.chensoul.auth.model.LoggedUser;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 登录校验
 */
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		if (StringUtils.isBlank(username)) {
			throw new UsernameNotFoundException("请输入用户名");
		}

		final User user = this.userMapper.loadUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户名或密码错误");
		}
		final LoggedUser loggedUser = new LoggedUser();
		BeanUtils.copyProperties(user, loggedUser);
		return loggedUser;
	}

}
