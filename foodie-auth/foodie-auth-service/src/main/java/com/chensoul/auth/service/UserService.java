package com.chensoul.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chensoul.auth.entity.User;
import com.chensoul.auth.mapper.UserMapper;
import com.chensoul.auth.model.dto.UserAddRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

;

/**
 * 食客服务业务逻辑层
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> implements IService<User> {
	@Resource
	private VerifyCodeService verifyCodeService;

	/**
	 * 校验手机号是否已注册
	 */
	public void checkPhoneIsRegistered(final String phone) {
		final User users = this.baseMapper.getByPhone(phone);
		Assert.isTrue(users != null, "该手机号未注册");
	}

	/**
	 * 用户注册
	 *
	 * @param userAddRequest
	 * @return
	 */
	public void register(final UserAddRequest userAddRequest) {
		final String username = userAddRequest.getUsername().trim();
		final String password = userAddRequest.getPassword().trim();
		final String phone = userAddRequest.getPhone();

		final String code = this.verifyCodeService.getCodeByPhone(phone);

		Assert.hasLength(code, "验证码已过期，请重新发送");
		Assert.isTrue(userAddRequest.getVerifyCode().equals(code), "验证码不一致，请重新输入");

		final User user = this.baseMapper.getByUsername(username);
		Assert.isTrue(user != null, "用户名已存在，请重新输入");

		userAddRequest.setPassword(new BCryptPasswordEncoder().encode(password));
		this.baseMapper.save(userAddRequest);
	}

	public List<User> findByIds(final Collection userIds) {
		if (CollectionUtils.isEmpty(userIds)) {
			return new ArrayList<>();
		}
		return this.baseMapper.selectBatchIds(userIds);
	}

}
