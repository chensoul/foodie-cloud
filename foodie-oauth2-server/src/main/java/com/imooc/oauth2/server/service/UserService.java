package com.imooc.oauth2.server.service;

import com.imooc.commons.model.entity.User;
import com.imooc.oauth2.server.mapper.UserMapper;
import com.imooc.oauth2.server.model.LoggedUserDetails;
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
public class UserService implements UserDetailsService {
	private UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		if (StringUtils.isBlank(username)) {
			throw new UsernameNotFoundException("请输入用户名");
		}

		final User user = this.userMapper.getByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户名或密码错误，请重新输入");
		}
		final LoggedUserDetails loggedUserDetails = new LoggedUserDetails();
		BeanUtils.copyProperties(user, loggedUserDetails);
		return loggedUserDetails;
	}

}
