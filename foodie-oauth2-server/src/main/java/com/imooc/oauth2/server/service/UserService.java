package com.imooc.oauth2.server.service;

import com.imooc.commons.model.entity.Diner;
import com.imooc.oauth2.server.mapper.AccountMapper;
import com.imooc.oauth2.server.model.DinerUserDetails;
import javax.annotation.Resource;
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
public class UserService implements UserDetailsService {

	@Resource
	private AccountMapper accountMapper;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		if (StringUtils.isBlank(username)) {
			throw new UsernameNotFoundException("请输入用户名");
		}

		final Diner diner = this.accountMapper.getByAccount(username);
		if (diner == null) {
			throw new UsernameNotFoundException("用户名或密码错误，请重新输入");
		}
		final DinerUserDetails dinerUserDetails = new DinerUserDetails();
		BeanUtils.copyProperties(diner, dinerUserDetails);
		return dinerUserDetails;
	}

}
