package com.imooc.diner.service;

import com.imooc.commons.constant.RedisKeyConstant;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 发送验证码业务逻辑层
 */
@Service
public class SendVerifyCodeService {

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 发送验证码
	 *
	 * @param phone
	 */
	public void send(final String phone) {
		if (!this.checkCodeIsExpired(phone)) {
			return;
		}
		final String code = RandomStringUtils.randomNumeric(6);
		final String key = RedisKeyConstant.verify_code.getKey() + phone;
		this.redisTemplate.opsForValue().set(key, code, 60, TimeUnit.SECONDS);
	}

	/**
	 * 根据手机号查询是否已生成验证码
	 *
	 * @param phone
	 * @return
	 */
	private boolean checkCodeIsExpired(final String phone) {
		final String key = RedisKeyConstant.verify_code.getKey() + phone;
		final String code = this.redisTemplate.opsForValue().get(key);
		return StringUtils.isBlank(code) ? true : false;
	}

	/**
	 * 根据手机号获取验证码
	 *
	 * @param phone
	 * @return
	 */
	public String getCodeByPhone(final String phone) {
		final String key = RedisKeyConstant.verify_code.getKey() + phone;
		return this.redisTemplate.opsForValue().get(key);
	}

}
