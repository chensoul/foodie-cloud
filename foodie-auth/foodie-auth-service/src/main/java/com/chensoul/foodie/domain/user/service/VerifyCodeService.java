package com.chensoul.foodie.domain.user.service;

import com.chensoul.foodie.constant.RedisKeyConstant;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 发送验证码业务逻辑层
 */
@Service
@AllArgsConstructor
public class VerifyCodeService {
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 发送验证码
	 *
	 * @param phone
	 */
	public void send(final String phone) {
		if (!checkCodeIsExpired(phone)) {
			return;
		}
		final String code = RandomStringUtils.randomNumeric(6);
		final String key = RedisKeyConstant.VERIFY_CODE.getKey() + phone;
		redisTemplate.opsForValue().set(key, code, 60, TimeUnit.SECONDS);
	}

	/**
	 * 根据手机号查询是否已生成验证码
	 *
	 * @param phone
	 * @return
	 */
	private boolean checkCodeIsExpired(final String phone) {
		final String key = RedisKeyConstant.VERIFY_CODE.getKey() + phone;
		final String code = redisTemplate.opsForValue().get(key);
		return StringUtils.isBlank(code) ? true : false;
	}

	/**
	 * 根据手机号获取验证码
	 *
	 * @param phone
	 * @return
	 */
	public String getCodeByPhone(final String phone) {
		final String key = RedisKeyConstant.VERIFY_CODE.getKey() + phone;
		return redisTemplate.opsForValue().get(key);
	}

}
