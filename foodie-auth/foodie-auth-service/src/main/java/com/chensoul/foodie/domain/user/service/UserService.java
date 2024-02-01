package com.chensoul.foodie.domain.user.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chensoul.foodie.domain.user.entity.User;
import com.chensoul.foodie.domain.user.model.UserAddRequest;
import com.chensoul.foodie.domain.user.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

;

/**
 * 食客服务业务逻辑层
 */
@Service
@AllArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> implements IService<User> {
	private VerifyCodeService verifyCodeService;

	public void checkPhoneIsRegistered(final String phone) {
		final boolean exists = baseMapper.exists(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone));
		Assert.isTrue(!exists, "该手机号已注册");
	}

	public void register(final UserAddRequest userAddRequest) {
		final String username = userAddRequest.getUsername().trim();
		final String password = userAddRequest.getPassword().trim();
		final String phone = userAddRequest.getPhone();

		final String code = verifyCodeService.getCodeByPhone(phone);

		Assert.hasLength(code, "验证码已过期，请重新发送");
		Assert.isTrue(userAddRequest.getVerifyCode().equals(code), "验证码不一致，请重新输入");

		User user = baseMapper.loadUserByUsername(username);
		Assert.isTrue(user != null, "用户名已存在，请重新输入");

		userAddRequest.setPassword(new BCryptPasswordEncoder().encode(password));

		user = new User();
		BeanUtils.copyProperties(userAddRequest, user);
		baseMapper.insert(user);
	}

}
